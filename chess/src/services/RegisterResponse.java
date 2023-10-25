package services;

/**
 * Represents the result of a registration request.
 */
public class RegisterResponse {
    /**
     * The authentication token for the registered user.
     */
    private String authToken;
    /**
     * The username of the registered user.
     */
    private String username;
    /**
     * The error message.
     */
    private String message;
    /**
     * Indicates if the register operation was successful.
     */
    private boolean success;

    /**
     * Default constructor.
     */
    public RegisterResponse() { }

    /**
     * Constructs a new RegisterResponse indicating success.
     *
     * @param authToken The authentication token for the registered user.
     * @param username The username of the registered user.
     */
    public RegisterResponse(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    /**
     * Constructs a new RegisterResponse indicating an error.
     *
     * @param message The error message.
     */
    public RegisterResponse(String message) {
        this.message = message;
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the message related to an operation or event.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message related to an operation or event.
     *
     * @param message The message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the authentication token associated with a user or session.
     *
     * @return The authentication token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authentication token for a user or session.
     *
     * @param authToken The authentication token to be set.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Retrieves the username of a user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of a user.
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
