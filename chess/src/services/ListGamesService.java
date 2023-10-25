package services;

import models.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides services to list all games.
 */
public class ListGamesService {
    /**
     * In-memory storage for the games.
     */
    private static final List<Game> games = new ArrayList<>();

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
        // Validate the authToken
        if ("valid_token".equals(request.getAuthToken())) {
            return new ListGamesResponse(new ArrayList<>(games)); // Return a copy of the games list
        } else {
            // Consider returning a more descriptive response
            ListGamesResponse response = new ListGamesResponse();
            response.setMessage("Invalid authentication token.");
            return response;
        }
    }
}
