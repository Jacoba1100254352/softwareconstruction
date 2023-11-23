package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
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
     * Logs-in a user.
     *
     * @param request The login request containing user credentials.
     * @return LoginResponse indicating success or failure.
     */
    public LoginResponse login(LoginRequest request) {
        try {
            if (userDAO.validatePassword(request.getUsername(), request.getPassword())) {
                User user = userDAO.getUser(request.getUsername());
                AuthToken newToken = new AuthToken(UUID.randomUUID().toString(), request.getUsername());
                authDAO.insertAuth(newToken);

                System.out.println("user: " + user.toString());
                System.out.println("isAdmin: " + user.getIsAdmin());

                // Include isAdmin in the response
                return new LoginResponse(newToken.getToken(), user.getUsername(), user.getIsAdmin());
            } else {
                return new LoginResponse("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            return new LoginResponse("Error: " + e.getMessage());
        }
    }
}
