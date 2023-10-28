package dataAccess;


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
     * @param token  The user's authentication token.
     * @param username The username for the associated user.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void insertAuth(String token, String username) throws DataAccessException {
        if (tokenStorage.containsToken(token))
            throw new DataAccessException("Token already exists.");

        tokenStorage.addToken(token, username);
    }

    /**
     * Finds a user token.
     *
     * @param token The authentication token to be found.
     * @return Username associated with the token or null if not found.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public String findAuth(String token) throws DataAccessException {
        return tokenStorage.getUsernameForToken(token);
    }

    /**
     * Removes a token.
     *
     * @param token The authentication token to be removed.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void deleteAuth(String token) throws DataAccessException {
        if (!tokenStorage.containsToken(token))
            throw new DataAccessException("Token not found.");

        tokenStorage.removeToken(token);
    }

    /**
     * Clears all data from the database.
     */
    public void clearAuth() {
        tokenStorage.clearTokens();
    }
}
