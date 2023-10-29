package requests;

/**
 * Represents the request data required for logging out a user.
 */
public class LogoutRequest {

    /**
     * The authentication token of the user trying to logout.
     */
    private String authToken;

    /**
     * Default constructor.
     */
    public LogoutRequest() {
    }

    /**
     * Constructs a new LogoutRequest with the given authToken.
     *
     * @param authToken The authentication token of the user.
     */
    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }


    ///   Getters and setters   ///

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

