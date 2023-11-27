package requests;

/**
 * Represents the request data required to clear the database.
 */
public class DeleteUserRequest {

    /**
     * The authentication token of the user trying to delete the specified user.
     */
    private String authToken;

    /**
     * The username of the user to be deleted.
     */
    private String username;


    ///   Constructor   ///

    /**
     * Constructor for the clear request authToken. (Admin authToken)
     *
     * @param authToken The authentication token of the user.
     */
    public DeleteUserRequest(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
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
}
