package dataAccess;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for handling authentication-related data operations.
 */
public class AuthDAO {

    /**
     * In-memory storage for auth tokens and associated usernames
     */
    private Map<String, String> tokenToUsernameMap;

    /**
     * Default constructor.
     */
    public AuthDAO() {
        tokenToUsernameMap = new HashMap<>();
    }

    /**
     * Inserts a new token for a user.
     *
     * @param token  The user's authentication token.
     * @param username The username for the associated user.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void insert(String token, String username) throws DataAccessException {
        if (tokenToUsernameMap.containsKey(token)) {
            throw new DataAccessException("Token already exists.");
        }
        tokenToUsernameMap.put(token, username);
    }

    /**
     * Finds a user token.
     *
     * @param token The authentication token to be found.
     * @return Username associated with the token or null if not found.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public String find(String token) throws DataAccessException {
        return tokenToUsernameMap.get(token);
    }

    /**
     * Removes a token.
     *
     * @param token The authentication token to be removed.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void delete(String token) throws DataAccessException {
        if (!tokenToUsernameMap.containsKey(token)) {
            throw new DataAccessException("Token not found.");
        }
        tokenToUsernameMap.remove(token);
    }

    /**
     * Clears all data from the database.
     *
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void clear() throws DataAccessException {
        tokenToUsernameMap.clear();
    }
}
