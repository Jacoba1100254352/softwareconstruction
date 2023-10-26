package services;

import storage.*;

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
        TokenStorage tokens = StorageManager.getInstance().getTokenStorage();

        if (tokens.containsToken(request.getAuthToken())) {
            tokens.removeToken(request.getAuthToken());
            return new LogoutResponse(true, "Logged out successfully.");
        } else return new LogoutResponse(false, "Invalid authentication token.");
    }
}
