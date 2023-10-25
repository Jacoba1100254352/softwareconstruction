package services;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides services to clear the application's database.
 */
public class ClearService {
    /**
     * In-memory database storage
     */
    private static final List<Object> database = new ArrayList<>();

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
            database.clear();
            return new ClearResponse(true, "Database cleared successfully.");
        } else {
            return new ClearResponse(false, "Invalid authentication token.");
        }
    }
}
