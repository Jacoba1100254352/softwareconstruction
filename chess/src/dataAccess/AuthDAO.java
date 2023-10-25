package dataAccess;

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
    public void insert(String token, String userId) throws DataAccessException {

    }

    /**
     * Finds a user token.
     *
     * @param token The authentication token to be found.
     * @return UserId associated with the token or null if not found.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public String find(String token) throws DataAccessException {
        return null;
    }

    /**
     * Removes a token.
     *
     * @param token The authentication token to be removed.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void delete(String token) throws DataAccessException {

    }

    /**
     * Clears all data from the database.
     *
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void clear() throws DataAccessException {

    }
}
