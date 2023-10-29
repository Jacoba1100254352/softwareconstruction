package dataAccess;

import models.AuthToken;
import storage.StorageManager;
import storage.TokenStorage;

/**
 * Responsible for handling authentication-related data operations.
 */
public class AuthDAO {

    /**
     * In-memory storage for auth tokens and associated usernames
     */
    private final TokenStorage tokenStorage;

    /**
     * Default constructor.
     */
    public AuthDAO() {
        this.tokenStorage = StorageManager.getInstance().getTokenStorage();
    }

    /**
     * Inserts a new token for a user.
     *
     * @param authToken The user's authentication token.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void insertAuth(AuthToken authToken) throws DataAccessException {
        if (tokenStorage.containsToken(authToken.getToken()))
            throw new DataAccessException("Token already exists.");

        tokenStorage.addToken(authToken);
    }

    /**
     * Finds a user token.
     *
     * @param authToken The authentication token to be found.
     * @return Username associated with the token or null if not found.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public AuthToken findAuth(String authToken) throws DataAccessException {
        return tokenStorage.getToken(authToken);
    }

    /**
     * Removes a token.
     *
     * @param authToken The authentication token to be removed.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void deleteAuth(AuthToken authToken) throws DataAccessException {
        if (!tokenStorage.containsToken(authToken.getToken()))
            throw new DataAccessException("Token not found.");

        tokenStorage.removeToken(authToken);
    }

    /**
     * Clears all data from the database.
     */
    public void clearAuth() {
        tokenStorage.clearTokens();
    }
}
