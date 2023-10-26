package services;

import models.Game;
import storage.GameStorage;
import storage.StorageManager;

/**
 * Provides services to create a new game.
 */
public class CreateGameService {
    /**
     * In-memory storage for games
     */
    GameStorage gameStorage = StorageManager.getInstance().getGameStorage();

    /**
     * Default constructor.
     */
    public CreateGameService() { }

    /**
     * Creates a new game based on the provided request.
     *
     * @param request The request containing game details.
     * @return CreateGameResponse with the result of the operation.
     */
    public CreateGameResponse createGame(CreateGameRequest request) {
        // Validate the authToken
        if ("valid_token".equals(request.getAuthToken())) {
            Game newGame = new Game(gameStorage.getNextGameId(), request.getGameName());
            gameStorage.getGames().put(newGame.getGameID(), newGame);
            return new CreateGameResponse(newGame.getGameID());
        } else {
            return null;  // For simplicity, return null on failure. Consider using a more descriptive response.
        }
    }
}
