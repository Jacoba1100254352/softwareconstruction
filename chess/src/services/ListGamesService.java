package services;

import storage.GameStorage;
import storage.StorageManager;
import storage.TokenStorage;

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
        TokenStorage tokenStorage = StorageManager.getInstance().getTokenStorage();
        if (!tokenStorage.containsToken(request.getAuthToken())) {
            ListGamesResponse errorResponse = new ListGamesResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error: unauthorized");
            return errorResponse;
        }

        try {
            return new ListGamesResponse(new ArrayList<>(gameStorage.getGames().values()));
        } catch (Exception e) {
            ListGamesResponse errorResponse = new ListGamesResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error: " + e.getMessage());
            return errorResponse;
        }
    }

}
