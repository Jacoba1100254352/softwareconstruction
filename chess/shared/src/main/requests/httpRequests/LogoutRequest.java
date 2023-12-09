package requests.httpRequests;

/**
 * Represents the request data required for logging out a user.
 */
public class LogoutRequest {

    /**
     * The authentication token of the user trying to log-out.
     */
    private String authToken;


    ///   Constructors   ///

    /**
     * Constructor for the logout request authToken.
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

