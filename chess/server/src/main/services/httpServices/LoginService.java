package services.httpServices;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import requests.httpRequests.LoginRequest;
import responses.httpResponses.LoginResponse;

import java.util.UUID;

/**
 * Provides services for logging in a user.
 */
public class LoginService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

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
}
