package services;

import storage.GameStorage;
import storage.StorageManager;

import java.util.ArrayList;

/**
 * Provides services to list all games.
 */
public class ListGamesService {
    /**
     * In-memory storage for the games.
     */
    GameStorage gameStorage = StorageManager.getInstance().getGameStorage();

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
            return new ListGamesResponse(new ArrayList<>(gameStorage.getGames().values())); // Return a copy of the games list
        } else {
            ListGamesResponse response = new ListGamesResponse();
            response.setMessage("Invalid authentication token.");
            return response;
        }
    }
}
