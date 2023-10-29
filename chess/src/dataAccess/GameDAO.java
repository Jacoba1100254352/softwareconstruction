package dataAccess;

import chess.ChessGame;
import models.Game;
import storage.GameStorage;
import storage.StorageManager;

import java.util.Collection;

/**
 * DAO class for handling game-related data operations.
 */
public class GameDAO {

    /**
     * In-memory storage for games.
     */
    private final GameStorage gameStorage;

    /**
     * Default constructor.
     */
    public GameDAO() {
        gameStorage = StorageManager.getInstance().getGameStorage();
    }

    /**
     * Inserts a new game into the data store.
     *
     * @param game The game object to be inserted.
     * @throws DataAccessException if the operation fails.
     */
    public void insertGame(Game game) throws DataAccessException {
        if (gameStorage.containsGame(game.getGameID()))
            updateGame(game.getGameID(), game.getGame());

        gameStorage.addGame(game);
    }

    /**
     * Retrieves a specified game from the data store by gameID.
     *
     * @param gameID The ID of the game to retrieve.
     * @return The retrieved game object.
     * @throws DataAccessException if the operation fails.
     */
    public Game findGameById(Integer gameID) throws DataAccessException {
        Game game = gameStorage.getGame(gameID);
        if (game == null)
            throw new DataAccessException("Game not found.");

        return game;
    }

    /**
     * Retrieves all games from the data store.
     *
     * @return A list of all game objects.
     */
    public Collection<Game> findAllGames() {
        return gameStorage.getAllGames();
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
        Game game = findGameById(gameID);

        if (color == ChessGame.TeamColor.WHITE) {
            if (game.getWhiteUsername() != null)
                throw new DataAccessException("White player spot is already taken.");

            game.setWhiteUsername(username);
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (game.getBlackUsername() != null)
                throw new DataAccessException("Black player spot is already taken.");

            game.setBlackUsername(username);
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
        findGameById(gameID).setGame(newChessGame);
    }

    public void clearGames() {
        gameStorage.clearGames();
    }

    public Integer getNextGameID() {
        return gameStorage.getNextGameId();
    }
}
