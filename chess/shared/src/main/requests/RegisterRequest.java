package requests;

/**
 * Represents the request data required for registering a user.
 *
 * @param username The username of the user.
 * @param password The password of the user.
 * @param email    The email of the user.
 */
public record RegisterRequest(String username, String password, String email) {
}
