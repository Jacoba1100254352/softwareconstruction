package responses;

/**
 * Represents the result of a login request.
 */
public class LoginResponse implements Response {
    /**
     * The authentication token for the logged-in user.
     */
    private final String authToken;

    /**
     * The username of the logged-in user.
     */
    private final String username;

    /**
     * The error message.
     */
    private final String message;

    /**
     * Indicates if the login operation was successful.
     */
    private final boolean success;


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
        this.message = null;
        this.success = true;  // true: successful login
    }

    /**
     * Constructor for error messages.
     *
     * @param message The error message.
     */
    public LoginResponse(String message) {
        this.authToken = null;
        this.username = null;
        this.message = message;
        this.success = false; // false: failed login
    }


    ///   Getters and setters   ///

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public boolean success() {
        return success;
    }
}
