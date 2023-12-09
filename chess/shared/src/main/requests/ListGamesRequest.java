package requests;

/**
 * Represents the request data required to list all games.
 */
public class ListGamesRequest {

    /**
     * The authentication token of the user trying to list games.
     */
    private String authToken;


    ///   Constructor   ///

    /**
     * Constructor for the logout request authToken.
     *
     * @param authToken The authentication token of the user.
     */
    public ListGamesRequest(String authToken) {
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
