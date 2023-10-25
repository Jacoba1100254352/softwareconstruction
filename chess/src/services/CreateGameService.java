package services;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides services to create a new game.
 */
public class CreateGameService {
    /**
     * In-memory storage for games
     */
    private static final List<String> games = new ArrayList<>();
    /**
     * Count the game id's created
     */
    private static int idCounter = 0;

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
            games.add(request.getGameName());
            return new CreateGameResponse(idCounter++);
        } else {
            return null;  // For simplicity, return null on failure. Consider using a more descriptive response.
        }
    }
}
