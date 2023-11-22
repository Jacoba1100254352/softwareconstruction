package responses;

/**
 * Represents the result of a registration request.
 */
public class RegisterResponse implements Response {
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
     * Indicates the success of the register operation.
     */
    private boolean success;


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
        this.success = true;  // true: successful registration
    }

    /**
     * Constructor for error messages.
     *
     * @param message The error message.
     */
    public RegisterResponse(String message) {
        this.message = message;
        this.success = false; // false: failed registration
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
}
