package models;

/**
 * Represents a user with associated attributes.
 */
public class User {
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

    /**
     * Whether the user is an admin.
     */
    private boolean isAdmin;


    ///   Constructor   ///

    /**
     * Constructor for a new user with the given attributes.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email    The email address of the user.
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isAdmin = false;
    }

    /**
     * Constructor for a new user with the given attributes.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email    The email address of the user.
     * @param isAdmin  Whether the user is an admin.
     */
    public User(String username, String password, String email, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
