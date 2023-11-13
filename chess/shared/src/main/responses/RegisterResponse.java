package responses;

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


    ///   Constructors   ///

    /**
     * Constructor for successful response.
     *
     * @param authToken The authentication token for the registered user.
     * @param username  The username of the registered user.
     */
    public RegisterResponse(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    /**
     * Constructor for error messages.
     *
     * @param message The error message.
     */
    public RegisterResponse(String message) {
        this.message = message;
    }


    ///   Getters and setters   ///

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
