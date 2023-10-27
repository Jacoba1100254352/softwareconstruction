package services;

import models.Game;
import storage.*;

/**
 * Provides services to create a new game.
 */
public class CreateGameService {
    /**
     * In-memory storage for games
     */
    GameStorage gameStorage = StorageManager.getInstance().getGameStorage();
    /**
     * In-memory storage for tokens
     */
    TokenStorage tokenStorage = StorageManager.getInstance().getTokenStorage();

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
        if (request.getGameName() == null || request.getGameName().isEmpty()) {
            return new CreateGameResponse("Error: bad request");
        }

        if (!tokenStorage.containsToken(request.getAuthToken())) {
            return new CreateGameResponse("Error: unauthorized");
        }

        try {
            Game newGame = new Game(gameStorage.getNextGameId(), request.getGameName());
            gameStorage.getGames().put(newGame.getGameID(), newGame);
            return new CreateGameResponse(newGame.getGameID());
        } catch (Exception e) {
            return new CreateGameResponse("Error: " + e.getMessage());
        }
    }
}
