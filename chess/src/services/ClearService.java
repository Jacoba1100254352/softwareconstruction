package services;

import dataAccess.DataAccessException;
import dataAccess.Database;
import requests.ClearRequest;
import responses.ClearResponse;

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
    public ClearResponse clearDatabase(ClearRequest request) {
        try {
            // Call resetDatabase method to clear all data
            Database.getInstance().resetDatabase();
            return new ClearResponse(true, "Database cleared successfully.");
        } catch (DataAccessException e) {
            return new ClearResponse(false, "Error: " + e.getMessage());
        }
    }

}