package responses;

/**
 * Represents the result of a login request.
 *
 * @param authToken The authentication token for the logged-in user.
 * @param username  The username of the logged-in user.
 * @param message   The error message.
 * @param success   Indicates if the login operation was successful.
 */
public record LoginResponse(String authToken, String username, String message, boolean success) implements Response {
}
