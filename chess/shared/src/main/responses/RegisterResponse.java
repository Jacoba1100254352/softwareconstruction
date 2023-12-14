package responses;

/**
 * Represents the result of a registration request.
 */
public class RegisterResponse implements Response {
    /**
     * The authentication token for the registered user.
     */
    private final String authToken;

    /**
     * The username of the registered user.
     */
    private final String username;

    /**
     * The error message.
     */
    private final String message;

    /**
     * Indicates the success of the register operation.
     */
    private final boolean success;


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
        this.message = null;
        this.success = true;  // true: successful registration
    }

    /**
     * Constructor for error messages.
     *
     * @param message The error message.
     */
    public RegisterResponse(String message) {
        this.authToken = null;
        this.username = null;
        this.message = message;
        this.success = false; // false: failed registration
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
