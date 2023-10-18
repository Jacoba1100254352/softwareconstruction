package services;

/**
 * Represents the request data required for logging in a user.
 */
public class LoginRequest {
    private String username;
    private String password;

    /**
     * Default constructor for deserialization.
     */
    public LoginRequest() { }

    /**
     * Constructs a new LoginRequest with the given parameters.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the username of the user.
     *
     * @return A string representing the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username A string representing the username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the password of the user.
     *
     * @return A string representing the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password A string representing the password to be set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
