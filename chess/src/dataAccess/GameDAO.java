package dataAccess;

import chess.*;
import models.Game;

import java.util.List;

/**
 * DAO class for handling game-related data operations.
 */
public class GameDAO {

    /**
     * Default constructor.
     */
    public GameDAO() {}

    /**
     * Inserts a new game into the data store.
     *
     * @param game The game object to be inserted.
     * @throws DataAccessException if the operation fails.
     */
    public void insertGame(Game game) throws DataAccessException {

    }

    /**
     * Retrieves a specified game from the data store by gameID.
     *
     * @param gameID The ID of the game to retrieve.
     * @return The retrieved game object.
     * @throws DataAccessException if the operation fails.
     */
    public Game findGameById(String gameID) throws DataAccessException {

        return null;
    }

    /**
     * Retrieves all games from the data store.
     *
     * @return A list of all game objects.
     * @throws DataAccessException if the operation fails.
     */
    public List<Game> findAllGames() throws DataAccessException {

        return null;
    }

    /**
     * Claims a spot in a specified game.
     *
     * @param gameID The game ID of the spot to be claimed.
     * @param username The username of the player claiming the spot.
     * @param color The color (WHITE/BLACK) the player wants.
     * @throws DataAccessException if the operation fails.
     */
    public void claimSpot(String gameID, String username, ChessGame.TeamColor color) throws DataAccessException {

    }

    /**
     * Updates the chess game in the data store.
     *
     * @param gameID The ID of the game to be updated.
     * @param newChessGame The new ChessGame object to replace the existing one.
     * @throws DataAccessException if the operation fails.
     */
    public void updateGame(String gameID, ChessGame newChessGame) throws DataAccessException {

    }

    /**
     * Removes a game from the data store.
     *
     * @param gameID The ID of the game to be removed.
     * @throws DataAccessException if the operation fails.
     */
    public void removeGame(String gameID) throws DataAccessException {

    }

    /**
     * Clears all data from the data store.
     *
     * @throws DataAccessException if the operation fails.
     */
    public void clearAll() throws DataAccessException {

    }
}

