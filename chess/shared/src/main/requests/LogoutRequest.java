package requests;

/**
 * Represents the request data required for logging out a user.
 *
 * @param authToken The authentication token of the user trying to log-out.
 */
public record LogoutRequest(String authToken) {
}

