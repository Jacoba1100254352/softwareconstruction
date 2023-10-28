package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.Game;

import java.util.List;

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
                ListGamesResponse errorResponse = new ListGamesResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Error: unauthorized");
                return errorResponse;
            }
            List<Game> allGames = gameDAO.findAllGames();
            return new ListGamesResponse(allGames);
        } catch (DataAccessException e) {
            ListGamesResponse errorResponse = new ListGamesResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error: " + e.getMessage());
            return errorResponse;
        }
    }

}
