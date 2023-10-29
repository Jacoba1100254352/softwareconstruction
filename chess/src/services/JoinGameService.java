package services;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import requests.JoinGameRequest;
import responses.JoinGameResponse;

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
    public JoinGameService() {
    }

    /**
     * Allows a user to join a game.
     *
     * @param request The request to join a game.
     * @return JoinGameResponse indicating success or failure.
     */
    public JoinGameResponse joinGame(JoinGameRequest request) {
        try {
            // 1. Verify user's identity using the auth token
            String username = authDAO.findAuth(request.getAuthToken());
            if (username == null)
                return new JoinGameResponse(false, "Error: unauthorized");

            // 2. Check if the specified game exists
            if (gameDAO.findGameById(request.getGameID()) == null)
                return new JoinGameResponse(false, "Error: bad request");

            // 3. Check if color is specified and handle accordingly
            if (request.getPlayerColor().equalsIgnoreCase("WHITE"))
                gameDAO.claimSpot(request.getGameID(), username, ChessGame.TeamColor.WHITE);
            else if (request.getPlayerColor().equalsIgnoreCase("BLACK"))
                gameDAO.claimSpot(request.getGameID(), username, ChessGame.TeamColor.BLACK);
            else // User is watching the game; no changes are made to the game's data structure
                return new JoinGameResponse(true, "Successfully watching the game");

            return new JoinGameResponse(true, "Successfully joined the game");

        } catch (DataAccessException e) {
            if (e.getMessage().contains("already taken"))
                return new JoinGameResponse(false, "Error: already taken");
            else if (e.getMessage().contains("not found"))
                return new JoinGameResponse(false, "Error: bad request");
            else
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
