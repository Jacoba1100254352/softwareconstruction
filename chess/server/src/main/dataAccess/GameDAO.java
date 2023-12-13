package dataAccess;

import adapter.ChessBoardTypeAdapter;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGameImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for managing game-related data operations in the database.
 */
public class GameDAO {

    private final Database db;
    private final Gson gson;

    /**
     * Constructs a GameDAO with a reference to the database instance and a Gson instance for serialization.
     */
    public GameDAO() {
        this.db = Database.getInstance();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                .create();
    }

    /**
     * Serializes a ChessGame object into a JSON string.
     *
     * @param game The ChessGame object to serialize.
     * @return A JSON string representing the ChessGame object.
     */
    public String serializeChessGame(ChessGame game) {
        return this.gson.toJson(game);
    }

    /**
     * Deserializes a JSON string into a ChessGameImpl object.
     *
     * @param gameData The JSON string representing a ChessGame.
     * @return The ChessGameImpl object.
     */
    private ChessGameImpl deserializeChessGame(String gameData) {
        return this.gson.fromJson(gameData, ChessGameImpl.class);
    }

    /**
     * Inserts a new game into the database.
     *
     * @param game The game object to be inserted.
     * @throws DataAccessException if the operation fails.
     */
    public void insertGame(Game game) throws DataAccessException {
        String sql = "INSERT INTO Games (GameName, WhiteUsername, BlackUsername, GameState) VALUES (?, ?, ?, ?);";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            db.startTransaction(conn); // Start transaction

            prepareGameStatement(game, stmt);

            if (stmt.executeUpdate() == 0) throw new DataAccessException("Creating game failed, no rows affected.");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next())
                    game.setGameID(generatedKeys.getInt(1));
                else
                    throw new DataAccessException("Creating game failed, no ID obtained.");
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting game: " + e.getMessage());
        }
    }


    /**
     * Retrieves a specified game from the data store by gameID.
     *
     * @param gameID The ID of the game to retrieve.
     * @return The retrieved game object.
     * @throws DataAccessException if the operation fails.
     */
    public Game findGameByID(Integer gameID) throws DataAccessException {
        String sql = "SELECT * FROM Games WHERE GameID = ?;";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            return (rs.next()) ? new Game(
                    rs.getInt("GameID"),
                    rs.getString("GameName"),
                    rs.getString("WhiteUsername"),
                    rs.getString("BlackUsername"),
                    deserializeChessGame(rs.getString("GameState"))
            ) : null;
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding game: " + e.getMessage());
        }
    }

    /**
     * Retrieves all games from the data store.
     *
     * @return A list of all game objects.
     */
    public List<Game> findAllGames() throws DataAccessException {
        String sql = "SELECT * FROM Games;";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Game> games = new ArrayList<>();
            while (rs.next())
                games.add(new Game(
                        rs.getInt("GameID"),
                        rs.getString("GameName"),
                        rs.getString("WhiteUsername"),
                        rs.getString("BlackUsername"),
                        deserializeChessGame(rs.getString("GameState"))
                ));
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving all games: " + e.getMessage());
        }
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

            if (stmt.executeUpdate() == 0)
                throw new DataAccessException(color + " player spot is already taken or game does not exist.");
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while claiming spot: " + e.getMessage());
        }
    }

    /**
     * Updates the chess game in the data store.
     *
     * @param game The game object to be inserted.
     * @throws DataAccessException if the operation fails.
     */
    public void updateGame(Game game) throws DataAccessException {
        String sql = "UPDATE Games SET GameName = ?, WhiteUsername = ?, BlackUsername = ?, GameState = ? WHERE GameID = ?;";
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                prepareGameStatement(game, stmt);
                stmt.setInt(5, game.getGameID());

                if (stmt.executeUpdate() == 0) throw new DataAccessException("Updating game failed, no rows affected.");

                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                throw new DataAccessException("Error encountered while updating game: " + e.getMessage());
            }
        } catch (SQLException e) {
            db.rollback(conn, e);
            throw new DataAccessException("Error encountered while executing rollback: " + e.getMessage());
        } finally {
            db.closeConnection(conn);
        }
    }

    /**
     * Prepares a PreparedStatement with game data for insert or update operations.
     *
     * @param game The game object containing the data.
     * @param stmt The PreparedStatement to prepare.
     * @throws SQLException if a database access error occurs or this method is called on a closed PreparedStatement.
     */
    private void prepareGameStatement(Game game, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, game.getGameName());
        stmt.setString(2, game.getWhiteUsername());
        stmt.setString(3, game.getBlackUsername());
        stmt.setString(4, serializeChessGame(game.getGame()));
    }

    /**
     * Clear the games from the database.
     */
    public void clearGames(Connection conn) throws DataAccessException {
        String sql = "DELETE FROM Games;";
        try (Statement stmt = conn.createStatement()) {
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
            if (rs.next())
                return rs.getInt("GameID");
            else // No games exist, this will belong to the first.
                throw new DataAccessException("Error retrieving id, no games in the database.");

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving the current game ID: " + e.getMessage());
        }
    }
}
