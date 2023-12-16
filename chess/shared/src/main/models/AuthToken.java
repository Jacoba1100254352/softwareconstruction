package models;

/**
 * Represents an authentication token with associated attributes.
 */
public class AuthToken {
    /**
     * The token string.
     */
    private final String authToken;
    /**
     * The associated username.
     */
    private final String username;


    ///   Constructor   ///

    /**
     * Constructor for a new authentication token with the given attributes.
     *
     * @param authToken The token string.
     * @param username  The associated username.
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
     * Gets the username associated with the token.
     *
     * @return A string representing the username.
     */
    public String getUsername() {
        return this.username;
    }
}
