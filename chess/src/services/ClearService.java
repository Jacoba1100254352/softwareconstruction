package services;

import dataAccess.*;
import requests.ClearRequest;
import responses.ClearResponse;

/**
 * Provides services to clear the application's database.
 */
public class ClearService {
    private final UserDAO userDAO = new UserDAO();
    private final GameDAO gameDAO = new GameDAO();
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * Default constructor.
     */
    public ClearService() {

    }

    /**
     * Clears the entire database.
     *
     * @param request The clear request containing the authToken of the user.
     * @return ClearResponse indicating success or failure.
     */
    public ClearResponse clearDatabase(ClearRequest request) {
        try {
            /*
            // Verification step
            if (authDAO.findAuth(request.getAuthToken()) == null)
                return new ClearResponse(false, "Error: unauthorized");
             */

            // Assuming the verification succeeds, clear the database
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuth();
            return new ClearResponse(true, "Database cleared successfully.");
        } catch (DataAccessException e) {
            return new ClearResponse(false, "Error: " + e.getMessage());
        }
    }
}