package services;

import dataAccess.*;

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
    public ClearService() { }

    /**
     * Clears the entire database.
     *
     * @param request The request to clear the database.
     * @return ClearResponse indicating success or failure.
     */
    public ClearResponse clearDatabase(ClearRequest request) {
        try {
            if (authDAO.findAuth(request.getAuthToken()) != null) {
                userDAO.clearUsers();
                gameDAO.clearGames();
                authDAO.clearAuth();
                return new ClearResponse(true, "Database cleared successfully.");
            } else {
                return new ClearResponse(false, "Error: Invalid authentication token.");
            }
        } catch (DataAccessException e) {
            return new ClearResponse(false, "Error: " + e.getMessage());
        }
    }
}
