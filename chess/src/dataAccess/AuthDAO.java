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
     * @param token  The authentication token for the user.
     * @param userId The ID of the user for which the token is associated.
     */
    public void insertToken(String token, String userId) {

    }

    /**
     * Removes a token, typically during logout.
     * @param token The authentication token to be removed.
     */
    public void deleteToken(String token) {

    }

    /**
     * Validates if a given token is still valid.
     * @param token The authentication token to be validated.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        return false;
    }
}