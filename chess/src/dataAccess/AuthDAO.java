package dataAccess;

import models.Game;

import java.util.List;

/**
 * Responsible for handling authentication-related data operations.
 */
public class AuthDAO {

    /**
     * Default constructor.
     */
    public AuthDAO() {

    }

    /**
     * Inserts a new token for a user.
     *
     * @param token  The user's authentication token.
     * @param userId The user ID for the associated user.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void insertToken(String token, String userId) throws DataAccessException {

    }

    /**
     * Removes a token, typically during logout.
     *
     * @param token The authentication token to be removed.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void deleteToken(String token) throws DataAccessException {

    }

    /**
     * Validates if a given token is still valid.
     *
     * @param token The authentication token to be validated.
     * @return true if the token is valid, false otherwise.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public boolean validateToken(String token) throws DataAccessException {
        return false;
    }

    /**
     * Inserts a new game into the database.
     *
     * @param game The game object to be inserted.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void insertGame(Game game) throws DataAccessException {

    }

    /**
     * Retrieves a specified game from the database by gameID.
     *
     * @param gameId The ID of the game to be retrieved.
     * @return The game object, null if not found.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public Game findGame(String gameId) throws DataAccessException {
        return null;
    }

    /**
     * Retrieves all games from the database.
     *
     * @return A list of all games.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public List<Game> findAllGames() throws DataAccessException {
        return null;
    }

    /**
     * Claims a spot in the game.
     *
     * @param gameId The game ID of the spot to be claimed.
     * @param username The username of the player claiming the spot.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void claimSpot(String gameId, String username) throws DataAccessException {

    }

    /**
     * Updates a game in the database.
     *
     * @param gameId The ID of the game to be updated.
     * @param newGame The updated game object.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void updateGame(String gameId, Game newGame) throws DataAccessException {

    }

    /**
     * Removes a game from the database.
     *
     * @param gameId The ID of the game to be removed.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void removeGame(String gameId) throws DataAccessException {

    }

    /**
     * Clears all data from the database.
     *
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void clear() throws DataAccessException {

    }
}
