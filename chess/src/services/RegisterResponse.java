package services;

/**
 * Represents the result of a registration request.
 */
public class RegisterResponse {
    private String message;
    private String authToken;
    private String username;

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
     * @return A string representing the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message related to an operation or event.
     *
     * @param message A string representing the message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the authentication token associated with a user or session.
     *
     * @return A string representing the authentication token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authentication token for a user or session.
     *
     * @param authToken A string representing the authentication token to be set.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Retrieves the username of a user.
     *
     * @return A string representing the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of a user.
     *
     * @param username A string representing the username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
