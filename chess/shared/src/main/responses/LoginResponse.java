package responses;

/**
 * Represents the result of a login request.
 */
public class LoginResponse implements Response {
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
     * Indicates if the logged-in user is an admin.
     */
    private boolean isAdmin;


    ///   Constructors   ///

    /**
     * Constructor for successful response.
     *
     * @param authToken The authentication token for the logged-in user.
     * @param username  The username of the logged-in user.
     */
    public LoginResponse(String authToken, String username, boolean isAdmin) {
        this.authToken = authToken;
        this.username = username;
        this.message = null;
        this.success = true;  // true: successful login
        this.isAdmin = isAdmin;
    }

    /**
     * Constructor for error messages.
     *
     * @param message The error message.
     */
    public LoginResponse(String message) {
        this.message = message;
        this.success = false; // false: failed login
        this.isAdmin = false;
    }


    ///   Getters and setters   ///

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

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
