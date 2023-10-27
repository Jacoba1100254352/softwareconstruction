package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;

/**
 * Provides services to logout a user.
 */
public class LogoutService {

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
        AuthDAO authDAO = new AuthDAO();
        try {
            if (authDAO.find(request.getAuthToken()) == null) {
                return new LogoutResponse(false, "Error: unauthorized");
            }
            authDAO.delete(request.getAuthToken());
            return new LogoutResponse(true, "Logged out successfully.");
        } catch (DataAccessException e) {
            return new LogoutResponse(false, "Error: " + e.getMessage());
        }
    }
}
