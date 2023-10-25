package services;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides services to logout a user.
 */
public class LogoutService {
    /**
     * In-memory storage for the valid Tokens
     */
    private static final Set<String> validTokens = new HashSet<>();

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
        if (validTokens.contains(request.getAuthToken())) {
            validTokens.remove(request.getAuthToken());
            return new LogoutResponse(true, "Logged out successfully.");
        } else {
            return new LogoutResponse(false, "Invalid authentication token.");
        }
    }
}
