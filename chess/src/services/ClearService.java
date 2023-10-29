package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

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
     * @return ClearResponse indicating success or failure.
     */
    public ClearResponse clearDatabase() {
        try {
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuth();
            return new ClearResponse(true, "Database cleared successfully.");
        } catch (DataAccessException e) {
            return new ClearResponse(false, "Error: " + e.getMessage());
        }
    }
}
