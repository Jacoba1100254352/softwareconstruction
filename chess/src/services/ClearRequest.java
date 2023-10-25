package services;

/**
 * Represents the request data required to clear the database.
 */
public class ClearRequest {

    /**
     * The authentication token of the user trying to clear the database.
     */
    private String authToken;

    /**
     * Default constructor.
     */
    public ClearRequest() {}

    /**
     * Constructs a new ClearRequest with the given authToken.
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
