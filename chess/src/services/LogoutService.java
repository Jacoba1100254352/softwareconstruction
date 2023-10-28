package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;

/**
 * Provides services to logout a user.
 */
public class LogoutService {
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * Default constructor.
     */
    public LogoutService() { }

    /**
     * Logs out a user based on the provided request.
     *
     * @param request The logout request containing the authToken of the user.
     * @return LogoutResponse indicating success or failure.
     */
    public LogoutResponse logout(LogoutRequest request) {
        try {
            if (authDAO.findAuth(request.getAuthToken()) != null) {
                authDAO.deleteAuth(request.getAuthToken());
                return new LogoutResponse(true, "Logged out successfully.");
            } else {
                return new LogoutResponse(false, "Error: Invalid authentication token.");
            }
        } catch (DataAccessException e) {
            return new LogoutResponse(false, "Error: " + e.getMessage());
        }
    }

}
