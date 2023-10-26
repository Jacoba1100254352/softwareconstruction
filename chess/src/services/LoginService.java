package services;

import storage.*;
import models.User;

/**
 * Provides services for logging in a user.
 */
public class LoginService {
    /**
     * In-memory storage for the users and their login info
     */
    UserStorage users = StorageManager.getInstance().getUserStorage();
    /**
     * The success status of the login operation.
     */
    private boolean success;
    /**
     * The message associated with the login operation.
     */
    private String message;
    /**
     * The authentication token of the logged-in user.
     */
    private String authToken;

    /**
     * Default constructor.
     */
    public LoginService() { }

    /**
     * Logs a user in.
     *
     * @param request The login request containing user credentials.
     * @return LoginResponse indicating success or failure.
     */
    public LoginResponse login(LoginRequest request) {
        User user = users.getUsers().get(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            return new LoginResponse("valid_token", request.getUsername());
        } else {
            return new LoginResponse("Invalid username or password.");
        }
    }

    /**
     * Retrieves the success status of the login operation.
     *
     * @return A boolean indicating if the login was successful.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the login operation.
     *
     * @param success A boolean indicating if the login was successful.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Retrieves the message associated with the login operation.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the login operation.
     *
     * @param message The message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves the authentication token of the logged-in user.
     *
     * @return The authentication token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authentication token of the logged-in user.
     *
     * @param authToken The authentication token.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}