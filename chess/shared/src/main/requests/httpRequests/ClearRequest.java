package requests.httpRequests;

/**
 * Represents the request data required to clear the database.
 */
public class ClearRequest {

    /**
     * The authentication token of the user trying to clear the database.
     */
    private String authToken;


    ///   Constructor   ///

    /**
     * Constructor for the clear request authToken. (authToken here is yet unused)
     *
     * @param authToken The authentication token of the user.
     */
    public ClearRequest(String authToken) {
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
