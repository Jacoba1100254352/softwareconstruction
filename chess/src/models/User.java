package models;

/**
 * Represents a user with associated attributes.
 */
public class User {
    private String username;
    private String password;
    private String email;

    /**
     * Constructs a new user with the given attributes.
     *
     * @param username A string representing the username of the user.
     * @param password A string representing the password of the user.
     * @param email    A string representing the email address of the user.
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the username of the user.
     * @return A string representing the username.
     */
    public String getUsername() {
        return this.username;
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
     * Gets the password of the user.
     *
     * @return A string representing the password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password A string representing the password to be set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email of the user.
     *
     * @return A string representing the password.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email A string representing the email to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
