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


    ///   Constructors   ///

    /**
     * Constructor for successful response.
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
     * Constructor for error messages.
     *
     * @param message The error message.
     */
    public LoginResponse(String message) {
        this.message = message;
        this.success = false; // false: failed login
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
