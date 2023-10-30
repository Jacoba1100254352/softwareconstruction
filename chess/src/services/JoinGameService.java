package services;

import chess.ChessGame;
import dataAccess.*;
import models.*;
import requests.JoinGameRequest;
import responses.JoinGameResponse;

/**
 * Provides services for a user to join a game.
 */
public class JoinGameService {
    private final GameDAO gameDAO = new GameDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final UserDAO userDAO = new UserDAO();

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
            // Verify authToken exists
            AuthToken authToken = authDAO.findAuth(request.getAuthToken());
            if (authToken == null)
                return new JoinGameResponse(false, "Error: unauthorized");

            // Verify user exists
            User user = userDAO.getUser(authToken.getUsername());
            if (user == null)
                return new JoinGameResponse(false, "Error: user not found");

            // Verify the game exists
            if (gameDAO.findGameById(request.getGameID()) == null)
                return new JoinGameResponse(false, "Error: bad request");

            // Add player to game or watch status based on color
            if (request.getPlayerColor().equalsIgnoreCase("WHITE"))
                gameDAO.claimSpot(request.getGameID(), user.getUsername(), ChessGame.TeamColor.WHITE);
            else if (request.getPlayerColor().equalsIgnoreCase("BLACK"))
                gameDAO.claimSpot(request.getGameID(), user.getUsername(), ChessGame.TeamColor.BLACK);
            else // Watching
                return new JoinGameResponse(true, "Successfully watching the game");

            return new JoinGameResponse(true, "Successfully joined the game");

        } catch (DataAccessException e) {
            // Handle exceptions/errors
            if (e.getMessage().contains("already taken"))
                return new JoinGameResponse(false, "Error: already taken");
            else if (e.getMessage().contains("not found"))
                return new JoinGameResponse(false, "Error: bad request");
            else
                return new JoinGameResponse(false, "Error: " + e.getMessage());
        }
    }


    ///   Getters and setters   ///

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
