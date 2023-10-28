package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;

/**
 * Provides services to list all games.
 */
public class ListGamesService {
    private final GameDAO gameDAO = new GameDAO();
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * Default constructor.
     */
    public ListGamesService() { }

    /**
     * Lists all games for the authenticated user.
     *
     * @param request The request containing the authToken of the user.
     * @return ListGamesResponse containing a list of all games.
     */

    public ListGamesResponse listAllGames(ListGamesRequest request) {
        try {
            if (authDAO.findAuth(request.getAuthToken()) == null) {
                return new ListGamesResponse(false, "Error: unauthorized");
            }
            return new ListGamesResponse(gameDAO.findAllGames());
        } catch (DataAccessException e) {
            return new ListGamesResponse(false, "Error: " + e.getMessage());
        }
    }
}
