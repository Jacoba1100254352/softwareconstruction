package requests;


/**
 * Represents the request data required for logging in a user.
 *
 * @param username The username of the user.
 * @param password The password of the user.
 */
public record LoginRequest(String username, String password)
{
}
