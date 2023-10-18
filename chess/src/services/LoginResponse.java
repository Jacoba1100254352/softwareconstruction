package services;

/**
 * Represents the result of a login request.
 */
public class LoginResponse {
    private String message;
    private String authToken;
    private String username;

    /**
     * Default constructor for deserialization.
     */
    public LoginResponse() { }

    /**
     * Constructs a new LoginResponse indicating success.
     *
     * @param authToken The authentication token for the logged-in user.
     * @param username The username of the logged-in user.
     */
    public LoginResponse(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    /**
     * Constructs a new LoginResponse indicating an error.
     *
     * @param message The error message.
     */
    public LoginResponse(String message) {
        this.message = message;
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the message associated with the operation or request.
     *
     * @return A string representing the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the operation or request.
     *
     * @param message A string representing the message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the authentication token for the user session.
     *
     * @return A string representing the authentication token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authentication token for the user session.
     *
     * @param authToken A string representing the authentication token to be set.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Retrieves the username of the user associated with the operation or request.
     *
     * @return A string representing the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user associated with the operation or request.
     *
     * @param username A string representing the username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
