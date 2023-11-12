package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import models.AuthToken;
import requests.LogoutRequest;
import responses.LogoutResponse;

/**
 * Provides services to log out a user.
 */
public class LogoutService {
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * Default constructor.
     */
    public LogoutService() {

    }

    /**
     * Logs out a user based on the provided request.
     *
     * @param request The logout request with the user's authToken.
     * @return LogoutResponse indicating success or failure.
     */
    public LogoutResponse logout(LogoutRequest request) {
        try {
            AuthToken authToken = authDAO.findAuth(request.getAuthToken());
            if (authToken != null) {
                authDAO.deleteAuth(authToken);
                return new LogoutResponse(true, "Logged out successfully.");
            } else {
                return new LogoutResponse(false, "Error: Invalid authentication token.");
            }
        } catch (DataAccessException e) {
            return new LogoutResponse(false, "Error: " + e.getMessage());
        }
    }
}
