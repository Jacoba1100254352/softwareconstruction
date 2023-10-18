package models;

/**
 * Represents an authentication token with associated attributes.
 */
public class AuthToken {
    private String authToken;
    private String username;

    /**
     * Constructs an authentication token with the specified values.
     *
     * @param authToken The token string.
     * @param username The associated username.
     */
    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }


    ///   Getters and setters   ///

    /**
     * Gets the authentication token.
     *
     * @return A string representing the token.
     */
    public String getToken() {
        return this.authToken;
    }

    /**
     * Sets the authentication token.
     *
     * @param authToken A string representing the token to be set.
     */
    public void setToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Gets the username associated with the token.
     *
     * @return A string representing the username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username associated with the token.
     *
     * @param username A string representing the username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
