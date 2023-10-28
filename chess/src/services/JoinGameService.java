package services;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;

/**
 * Provides services for a user to join a game.
 */
public class JoinGameService {
    private final GameDAO gameDAO = new GameDAO();
    private final AuthDAO authDAO = new AuthDAO();

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
        // Ensure gameID and playerColor are valid. If not, return "Error: bad request".
        if (request.getGameId() <= 0 || (request.getPlayerColor() == null ||
                (!request.getPlayerColor().equalsIgnoreCase("WHITE") && !request.getPlayerColor().equalsIgnoreCase("BLACK")))) {
            return new JoinGameResponse(false, "Error: bad request");
        }

        // Check if the authToken is valid. If not, return "Error: unauthorized".
        String username;
        try {
            username = authDAO.findAuth(request.getAuthToken());
        } catch (DataAccessException e) {
            return new JoinGameResponse(false, "Error: unauthorized");
        }

        // If the authToken does not correspond to a username, also return "Error: unauthorized".
        if (username == null || username.isEmpty()) {
            return new JoinGameResponse(false, "Error: unauthorized");
        }

        try {
            gameDAO.claimSpot(
                    request.getGameId(),
                    username,
                    ChessGame.TeamColor.valueOf(request.getPlayerColor().toUpperCase())
            );
            return new JoinGameResponse(true, "");
        } catch (DataAccessException e) {
            if (e.getMessage().contains("already taken")) {
                return new JoinGameResponse(false, "Error: already taken");
            }
            return new JoinGameResponse(false, "Error: " + e.getMessage());
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
