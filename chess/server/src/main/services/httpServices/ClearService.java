package services.httpServices;

import dataAccess.DataAccessException;
import dataAccess.Database;
import requests.httpRequests.ClearRequest;
import responses.httpResponses.ClearResponse;
import responses.Response;

/**
 * Provides services to clear the application's database.
 */
public class ClearService {
    /**
     * Clears the entire database.
     *
     * @param request The clear request containing the authToken of the user.
     * @return ClearResponse indicating success or failure.
     */
    public Response clearDatabase(ClearRequest request) {
        try {
            // Call resetDatabase method to clear all data
            Database.getInstance().resetDatabase();
            return new ClearResponse("Database cleared successfully.", true);
        } catch (DataAccessException e) {
            return new ClearResponse("Error: " + e.getMessage(), false);
        }
    }

}