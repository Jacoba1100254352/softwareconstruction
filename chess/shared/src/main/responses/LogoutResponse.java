package responses;

/**
 * Represents the response to the logout request.
 *
 * @param message A message providing success or error info.
 * @param success Indicates the success of the logout operation.
 */
public record LogoutResponse(String message, boolean success) implements Response {
}
