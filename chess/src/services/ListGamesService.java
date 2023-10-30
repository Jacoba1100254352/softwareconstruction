package services;

import dataAccess.*;
import requests.ListGamesRequest;
import responses.ListGamesResponse;

/**
 * Provides services to list all games.
 */
public class ListGamesService {
    private final GameDAO gameDAO = new GameDAO();
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * Lists all games for the authenticated user.
     *
     * @param request The request containing the user's authToken.
     * @return ListGamesResponse containing a list of all games.
     */

    public ListGamesResponse listAllGames(ListGamesRequest request) {
        try {
            if (authDAO.findAuth(request.getAuthToken()) == null)
                return new ListGamesResponse(false, "Error: unauthorized");
            else
                return new ListGamesResponse(gameDAO.findAllGames());
        } catch (DataAccessException e) {
            return new ListGamesResponse(false, "Error: " + e.getMessage());
        }
    }
}
