package services;

import storage.StorageManager;

/**
 * Provides services to clear the application's database.
 */
public class ClearService {

    /**
     * Default constructor.
     */
    public ClearService() { }

    /**
     * Clears the entire database.
     *
     * @param request The request to clear the database.
     * @return ClearResponse indicating success or failure.
     */
    public ClearResponse clearDatabase(ClearRequest request) {
        // Validate the authToken (for simplicity, let's assume a valid token is "valid_token")
        if ("valid_token".equals(request.getAuthToken())) {
            // Clear user data
            StorageManager.getInstance().getUserStorage().getUsers().clear();

            // Clear game data
            StorageManager.getInstance().getGameStorage().getGames().clear();

            return new ClearResponse(true, "Database cleared successfully.");
        } else {
            return new ClearResponse(false, "Error: Invalid authentication token.");
        }
    }
}
