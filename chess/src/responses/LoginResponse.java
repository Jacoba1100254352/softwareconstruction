package responses;

/**
 * Represents the result of a login request.
 */
public class LoginResponse {
    /**
     * The authentication token for the logged-in user.
     */
    private String authToken;
    /**
     * The username of the logged-in user.
     */
    private String username;
    /**
     * The error message.
     */
    private String message;
    /**
     * Indicates if the login operation was successful.
     */
    private boolean success;

    /**
     * Default constructor.
     */
    public LoginResponse() {

    }

    /**
     * Constructs a new LoginResponse indicating success.
     *
     * @param authToken The authentication token for the logged-in user.
     * @param username  The username of the logged-in user.
     */
    public LoginResponse(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
        this.success = true;  // true: successful login
    }

    /**
     * Constructs a new LoginResponse indicating an error.
     *
     * @param message The error message.
     */
    public LoginResponse(String message) {
        this.message = message;
        this.success = false;  // false for error messages
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the message associated with the operation or request.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the operation or request.
     *
     * @param message The message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the authentication token for the user session.
     *
     * @return The authentication token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authentication token for the user session.
     *
     * @param authToken The authentication token to be set.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Retrieves the username of the user associated with the operation or request.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user associated with the operation or request.
     *
     * @param username The username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
