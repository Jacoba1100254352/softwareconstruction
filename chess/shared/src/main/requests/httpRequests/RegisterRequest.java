package requests.httpRequests;

/**
 * Represents the request data required for registering a user.
 */
public class RegisterRequest {
    /**
     * The username of the user.
     */
    private String username;
    /**
     * The password of the user.
     */
    private String password;
    /**
     * The email of the user.
     */
    private String email;


    ///   Constructors   ///

    /**
     * Constructor to format new user data.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email    The email of the user.
     */
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
