package services;

import models.Game;
import storage.GameStorage;
import storage.StorageManager;

/**
 * Provides services for a user to join a game.
 */
public class JoinGameService {
    /**
     * In-memory storage for the players in the games
     */
    GameStorage gameStorage = StorageManager.getInstance().getGameStorage();

    /**
     * The success status of the user joining the game.
     */
    private boolean success;
    /**
     * The message associated with the user's attempt to join the game.
     */
    private String message;

    /**
     * Default constructor.
     */
    public JoinGameService() { }

    /**
     * Allows a user to join a game.
     *
     * @param request The request to join a game.
     * @return JoinGameResponse indicating success or failure.
     */
    public JoinGameResponse joinGame(JoinGameRequest request) {
        Integer gameId = Integer.parseInt(request.getGameId());
        Game game = gameStorage.getGames().get(gameId);
        // NOTE: Consider: Game game = gameStorage.getGames().get(request.getGameId());

        // Check if game exists
        if (game != null) {
            if (game.getWhiteUsername().isEmpty()) {
                game.setWhiteUsername(request.getUsername());
                return new JoinGameResponse(true, "Joined game as White player.");
            } else if (game.getBlackUsername().isEmpty()) {
                game.setBlackUsername(request.getUsername());
                return new JoinGameResponse(true, "Joined game as Black player.");
            } else {
                return new JoinGameResponse(false, "Game is already full.");
            }
        } else {
            return new JoinGameResponse(false, "Game does not exist.");
        }
    }



    ///   Getters and setters   ///

    /**
     * Checks if the user was successful in joining the game.
     *
     * @return A boolean indicating whether the user successfully joined the game or not.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the user's attempt to join the game.
     *
     * @param success A boolean indicating the success status to be set.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Retrieves the message associated with the user's attempt to join the game.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the user's attempt to join the game.
     *
     * @param message The message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
