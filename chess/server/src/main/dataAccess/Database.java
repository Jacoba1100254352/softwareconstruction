package dataAccess;


import models.AuthToken;
import models.Game;
import models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Responsible for creating connections to the database. Connections should be closed after use, either by calling
 * {@link #closeConnection(Connection)} on the Database instance or directly on the connection.
 */
public class Database
{
	
	// mysql -u root -pnyvceB-gysvuq-gozne5
	
	private static final String DB_NAME = "chessServerDB";
	private static final String DB_USERNAME = System.getProperty("dbUsername", "root");
	private static final String DB_PASSWORD = System.getProperty("dbPassword", "nyvceB-gysvuq-gozne5");
	private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
	private static final String CONNECTION_URL = SERVER_URL + DB_NAME;
	private static final MemoryStore MEMORY_STORE = new MemoryStore();
	private static Database instance;
	private static boolean useInMemory = Boolean.getBoolean("chess.test.memoryDb");
	private boolean schemaInitialized = false;
	
	/**
	 * Get a Database instance
	 *
	 * @return Database instance
	 */
	public static synchronized Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		
		return instance;
	}

	public static synchronized void useInMemoryStoreForTests() {
		useInMemory = true;
		System.setProperty("chess.test.memoryDb", "true");
		MEMORY_STORE.clear();
		instance = new Database();
	}

	public boolean isInMemory() {
		return useInMemory;
	}

	public MemoryStore memoryStore() {
		return MEMORY_STORE;
	}
	
	/**
	 * Attempts to roll back the connection transaction.
	 *
	 * @param conn The connection to perform the rollback on.
	 * @param e    The SQLException that caused the rollback to occur.
	 *
	 * @throws DataAccessException if the rollback fails.
	 */
	public void rollback(Connection conn, SQLException e) throws DataAccessException {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.rollback();
			}
		} catch (SQLException ex) {
			throw new DataAccessException("Could not roll back transaction. Initial Exception: " + e.getMessage() + "\nAdditional Exception: " + ex.getMessage());
		}
	}
	
	/**
	 * High-level function using the DAO clear methods to reset the database.
	 *
	 * @throws DataAccessException if the database reset fails.
	 */
	public void resetDatabase() throws DataAccessException {
		if (isInMemory()) {
			MEMORY_STORE.clear();
			return;
		}

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection(); // Get the connection
			conn.setAutoCommit(false); // Ensure the entire operation is atomic
			
			stmt = conn.createStatement();
			
			// Disable foreign key checks for this session
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
			
			// Clear all tables that have foreign key relationships
			new AuthDAO().clearAuth(conn); // Clear AuthTokens first
			new GameDAO().clearGames(conn); // Clear Games (if they have foreign keys to Users)
			new UserDAO().clearUsers(conn); // Finally, clear Users
			
			// Re-enable foreign key checks
			stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
			
			conn.commit(); // Commit the changes if all operations were successful
			
		} catch (SQLException e) {
			try {
				conn.rollback(); // Roll back the changes on error
			} catch (SQLException ex) {
				throw new DataAccessException("Rollback failed: " + ex.getMessage());
			}
			throw new DataAccessException("Error resetting database: " + e.getMessage());
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) { /* ignored */ }
			}
			closeConnection(conn); // Close the connection
		}
	}
	
	
	/**
	 * Start a transaction.
	 *
	 * @throws DataAccessException if a data access error occurs.
	 */
	public void startTransaction(Connection conn) throws DataAccessException {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DataAccessException("Start transaction failed: " + e.getMessage());
		}
	}
	
	/**
	 * Gets a connection to the database.
	 *
	 * @return Connection the connection.
	 *
	 * @throws DataAccessException if a data access error occurs.
	 */
	public Connection getConnection() throws DataAccessException {
		if (isInMemory()) {
			throw new DataAccessException("SQL connections are not available when tests use the in-memory database.");
		}

		try {
			initializeSchema();
			return DriverManager.getConnection(CONNECTION_URL, DB_USERNAME, DB_PASSWORD);
		} catch (SQLException e) {
			throw new DataAccessException("Error connecting to the database: " + e.getMessage());
		}
	}
	
	/**
	 * Closes the specified connection.
	 *
	 * @param conn the connection to be closed.
	 *
	 * @throws DataAccessException if a data access error occurs while closing the connection.
	 */
	public void closeConnection(Connection conn) throws DataAccessException {
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
				
			} catch (SQLException e) {
				throw new DataAccessException("Error encountered while closing the connection: " + e.getMessage());
			}
		}
	}

	private synchronized void initializeSchema() throws SQLException {
		if (schemaInitialized) {
			return;
		}

		try (
				Connection conn = DriverManager.getConnection(SERVER_URL, DB_USERNAME, DB_PASSWORD);
				Statement stmt = conn.createStatement()
		) {
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
		}

		try (
				Connection conn = DriverManager.getConnection(CONNECTION_URL, DB_USERNAME, DB_PASSWORD);
				Statement stmt = conn.createStatement()
		) {
			stmt.executeUpdate("""
					CREATE TABLE IF NOT EXISTS Users (
					    Username VARCHAR(50) NOT NULL PRIMARY KEY,
					    Password VARCHAR(255) NOT NULL,
					    Email VARCHAR(100) NOT NULL UNIQUE,
					    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					    IsAdmin BOOLEAN NOT NULL DEFAULT FALSE
					);
					""");
			stmt.executeUpdate("""
					CREATE TABLE IF NOT EXISTS AuthTokens (
					    TokenID INT AUTO_INCREMENT PRIMARY KEY,
					    Token VARCHAR(255) NOT NULL UNIQUE,
					    Username VARCHAR(50) NOT NULL,
					    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					    FOREIGN KEY (Username) REFERENCES Users(Username)
					);
					""");
			stmt.executeUpdate("""
					CREATE TABLE IF NOT EXISTS Games (
					    GameID INT AUTO_INCREMENT PRIMARY KEY,
					    WhiteUsername VARCHAR(50),
					    BlackUsername VARCHAR(50),
					    GameName VARCHAR(255) NOT NULL,
					    ChessGame TEXT NOT NULL,
					    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					    LastMoveAt TIMESTAMP,
					    FOREIGN KEY (WhiteUsername) REFERENCES Users(Username),
					    FOREIGN KEY (BlackUsername) REFERENCES Users(Username)
					);
					""");
		}

		schemaInitialized = true;
	}

	public static class MemoryStore
	{
		private final Map<String, User> users = new HashMap<>();
		private final Map<String, AuthToken> authTokens = new HashMap<>();
		private final Map<Integer, Game> games = new HashMap<>();
		private int nextGameId = 1;

		public synchronized void clear() {
			authTokens.clear();
			games.clear();
			users.entrySet().removeIf(entry -> !entry.getValue().getIsAdmin());
			nextGameId = 1;
		}

		public synchronized void insertUser(User user) throws DataAccessException {
			if (users.containsKey(user.getUsername())) {
				throw new DataAccessException("Error encountered while inserting user " + user.getUsername() + ": Duplicate entry");
			}
			for (User existingUser : users.values()) {
				if (existingUser.getEmail().equals(user.getEmail())) {
					throw new DataAccessException("Error encountered while inserting user " + user.getUsername() + ": Duplicate email");
				}
			}
			users.put(user.getUsername(), copyUser(user));
		}

		public synchronized User getUser(String username) {
			User user = users.get(username);
			return user == null ? null : copyUser(user);
		}

		public synchronized void updateUser(User user) throws DataAccessException {
			if (!users.containsKey(user.getUsername())) {
				throw new DataAccessException("User update failed: User not found.");
			}
			users.put(user.getUsername(), copyUser(user));
		}

		public synchronized void deleteUser(String username) throws DataAccessException {
			if (users.remove(username) == null) {
				throw new DataAccessException("User deletion failed: User not found.");
			}
			authTokens.entrySet().removeIf(entry -> entry.getValue().getUsername().equals(username));
			for (Game game : games.values()) {
				if (username.equals(game.getWhiteUsername())) {
					game.setWhiteUsername(null);
				}
				if (username.equals(game.getBlackUsername())) {
					game.setBlackUsername(null);
				}
			}
		}

		public synchronized void insertAuth(AuthToken authToken) throws DataAccessException {
			if (!users.containsKey(authToken.getUsername())) {
				throw new DataAccessException("Error encountered while inserting auth token: User not found.");
			}
			if (authTokens.containsKey(authToken.getToken())) {
				throw new DataAccessException("Error encountered while inserting auth token: Duplicate token.");
			}
			authTokens.entrySet().removeIf(entry -> entry.getValue().getUsername().equals(authToken.getUsername()));
			authTokens.put(authToken.getToken(), authToken);
		}

		public synchronized AuthToken findAuth(String authToken) {
			return authTokens.get(authToken);
		}

		public synchronized void deleteAuth(AuthToken authToken) throws DataAccessException {
			if (authTokens.remove(authToken.getToken()) == null) {
				throw new DataAccessException("Token deletion failed: Token not found.");
			}
		}

		public synchronized void insertGame(Game game) throws DataAccessException {
			validateGameUser(game.getWhiteUsername());
			validateGameUser(game.getBlackUsername());

			game.setGameID(nextGameId++);
			games.put(game.getGameID(), copyGame(game));
		}

		public synchronized Game findGameByID(Integer gameID) {
			Game game = games.get(gameID);
			return game == null ? null : copyGame(game);
		}

		public synchronized List<Game> findAllGames() {
			List<Game> result = new ArrayList<>();
			for (Game game : games.values()) {
				result.add(copyGame(game));
			}
			return result;
		}

		public synchronized void claimSpot(Integer gameID, String username, chess.gameplay.ChessGame.TeamColor color) throws DataAccessException {
			Game game = games.get(gameID);
			if (game == null) {
				throw new DataAccessException("Game not found.");
			}
			if (username != null && !users.containsKey(username)) {
				throw new DataAccessException("User not found.");
			}

			if (color == chess.gameplay.ChessGame.TeamColor.WHITE) {
				if (username != null && game.getWhiteUsername() != null) {
					throw new DataAccessException("WHITE player spot is already taken or game does not exist.");
				}
				game.setWhiteUsername(username);
			} else {
				if (username != null && game.getBlackUsername() != null) {
					throw new DataAccessException("BLACK player spot is already taken or game does not exist.");
				}
				game.setBlackUsername(username);
			}
		}

		public synchronized void updateGame(Game game) throws DataAccessException {
			if (!games.containsKey(game.getGameID())) {
				throw new DataAccessException("Updating game failed, no rows affected.");
			}
			validateGameUser(game.getWhiteUsername());
			validateGameUser(game.getBlackUsername());
			games.put(game.getGameID(), copyGame(game));
		}

		public synchronized void clearGames() {
			games.clear();
			nextGameId = 1;
		}

		public synchronized Integer getCurrentGameId() throws DataAccessException {
			if (games.isEmpty()) {
				throw new DataAccessException("Error retrieving id, no games in the database.");
			}
			return games.keySet().stream().max(Integer::compareTo).orElseThrow();
		}

		private void validateGameUser(String username) throws DataAccessException {
			if (username != null && !users.containsKey(username)) {
				throw new DataAccessException("Error encountered while inserting game: User not found.");
			}
		}

		private User copyUser(User user) {
			return new User(user.getUsername(), user.getPassword(), user.getEmail(), user.getIsAdmin());
		}

		private Game copyGame(Game game) {
			return new Game(game.getGameID(), game.getGameName(), game.getWhiteUsername(), game.getBlackUsername(), game.getChessGame());
		}
	}
}
