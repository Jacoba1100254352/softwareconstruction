package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides services for a user to join a game.
 */
public class JoinGameService {
    /**
     * In-memory storage for the players in the games
     */
    private static final Map<String, List<String>> playersInGames = new HashMap<>();
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
        // Check if game exists
        if (playersInGames.containsKey(request.getGameId())) {
            playersInGames.get(request.getGameId()).add(request.getUsername());
            return new JoinGameResponse(true, "Joined game successfully.");
        } else return new JoinGameResponse(false, "Game does not exist.");
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
