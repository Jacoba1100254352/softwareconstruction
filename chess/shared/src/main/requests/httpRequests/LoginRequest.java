package requests.httpRequests;

/**
 * Represents the request data required for logging in a user.
 */
public class LoginRequest {
    /**
     * The username of the user.
     */
    private String username;
    /**
     * The password of the user.
     */
    private String password;


    ///   Constructors   ///

    /**
     * Constructor for user log-in data.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }


    ///   Getters and setters   ///

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
