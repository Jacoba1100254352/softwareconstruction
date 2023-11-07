package dataAccess;

import com.google.gson.Gson;
import models.Game;
import chess.ChessGame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for handling game-related data operations.
 */
public class GameDAO {

    /**
     * Database storage for games.
     */
    private final Database db;

    /**
     * Default constructor.
     */
    public GameDAO() {
        this.db = Database.getInstance();
    }

    private String serializeChessGame(ChessGame game) {
        // Use Gson or another library to serialize the game object
        return new Gson().toJson(game);
    }

    private ChessGame deserializeChessGame(String gameData) {
        // Use Gson or another library to deserialize the game data
        return new Gson().fromJson(gameData, ChessGame.class);
    }


    /**
     * Inserts a new game into the data store.
     *
     * @param game The game object to be inserted.
     * @throws DataAccessException if the operation fails.
     */
    public void insertGame(Game game) throws DataAccessException {
        String sql = "INSERT INTO Games (WhiteUsername, BlackUsername, GameState) VALUES (?, ?, ?);";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false); // Start transaction

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, serializeChessGame(game.getGame()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating game failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                game.setGameID(generatedKeys.getInt(1));
            } else {
                throw new DataAccessException("Creating game failed, no ID obtained.");
            }

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    throw new DataAccessException("Rollback failed: " + ex.getMessage());
                }
            }
            throw new DataAccessException("Error encountered while inserting into the database: " + e.getMessage());
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignored */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignored */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    /**
     * Retrieves a specified game from the data store by gameID.
     *
     * @param gameID The ID of the game to retrieve.
     * @return The retrieved game object.
     * @throws DataAccessException if the operation fails.
     */
    public Game findGameById(int gameID) throws DataAccessException {
        Game game = null;
        String sql = "SELECT * FROM Games WHERE GameID = ?;";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                game = new Game(
                        rs.getInt("GameID"),
                        rs.getString("GameName")
                );
                game.setWhiteUsername(rs.getString("WhiteUsername"));
                game.setBlackUsername(rs.getString("BlackUsername"));
                // game.setGame(ChessGame) // This would require a way to serialize/deserialize ChessGame objects
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding game: " + e.getMessage());
        }
        return game;
    }

    /**
     * Retrieves all games from the data store.
     *
     * @return A list of all game objects.
     */
    public List<Game> findAllGames() throws DataAccessException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM Games;";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Game game = new Game(
                        rs.getInt("GameID"),
                        rs.getString("GameName")
                );
                game.setWhiteUsername(rs.getString("WhiteUsername"));
                game.setBlackUsername(rs.getString("BlackUsername"));
                // game.setGame(ChessGame) // This would require a way to serialize/deserialize ChessGame objects
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving all games: " + e.getMessage());
        }
        return games;
    }

    /**
     * Claims a spot in a specified game.
     *
     * @param gameID   The game ID of the spot to be claimed.
     * @param username The username of the player claiming the spot.
     * @param color    The color (WHITE/BLACK) the player wants.
     * @throws DataAccessException if the operation fails.
     */
    public void claimSpot(Integer gameID, String username, ChessGame.TeamColor color) throws DataAccessException {
        String columnToUpdate = (color == ChessGame.TeamColor.WHITE) ? "WhiteUsername" : "BlackUsername";
        String sql = "UPDATE Games SET " + columnToUpdate + " = ? WHERE GameID = ? AND " + columnToUpdate + " IS NULL;";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException(color + " player spot is already taken or game does not exist.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while claiming spot: " + e.getMessage());
        }
    }

    /**
     * Updates the chess game in the data store.
     *
     * @param gameID       The ID of the game to be updated.
     * @param newChessGame The new ChessGame object to replace the existing one.
     * @throws DataAccessException if the operation fails.
     */
    public void updateGame(Integer gameID, ChessGame newChessGame) throws DataAccessException {
        String sql = "UPDATE Games SET GameState = ? WHERE GameID = ?;";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false); // Start transaction

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, serializeChessGame(newChessGame));
            stmt.setInt(2, gameID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Updating game failed, no rows affected.");
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    throw new DataAccessException("Rollback failed: " + ex.getMessage());
                }
            }
            throw new DataAccessException("Error encountered while updating game: " + e.getMessage());
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignored */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    /**
     * Clear the games from the database.
     */
    public void clearGames() throws DataAccessException {
        String sql = "DELETE FROM Games;";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing games: " + e.getMessage());
        }
    }

    /**
     * Retrieves the most recent game ID from the data store.
     *
     * @return The ID of the most recent game.
     * @throws DataAccessException if the operation fails.
     */
    public Integer getCurrentGameId() throws DataAccessException {
        String sql = "SELECT GameID FROM Games ORDER BY GameID DESC LIMIT 1;";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("GameID");
            } else {
                throw new DataAccessException("No games found in the database.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving the current game ID: " + e.getMessage());
        }
    }
}
