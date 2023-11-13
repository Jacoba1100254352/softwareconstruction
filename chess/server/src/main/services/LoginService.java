package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import requests.LoginRequest;
import responses.LoginResponse;

import java.util.UUID;

/**
 * Provides services for logging in a user.
 */
public class LoginService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * The success status of the login operation.
     */
    private boolean success;

    /**
     * The success or error message associated with the login operation.
     */
    private String message;

    /**
     * The authentication token of the logged-in user.
     */
    private String authToken;

    /**
     * Default constructor.
     */
    public LoginService() {

    }

    /**
     * Logs-in a user.
     *
     * @param request The login request containing user credentials.
     * @return LoginResponse indicating success or failure.
     */
    public LoginResponse login(LoginRequest request) {
        try {
            if (userDAO.validatePassword(request.getUsername(), request.getPassword())) {
                AuthToken newToken = new AuthToken(UUID.randomUUID().toString(), request.getUsername());
                authDAO.insertAuth(newToken);
                return new LoginResponse(newToken.getToken(), newToken.getUsername());
            } else {
                return new LoginResponse("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            return new LoginResponse("Error: " + e.getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
