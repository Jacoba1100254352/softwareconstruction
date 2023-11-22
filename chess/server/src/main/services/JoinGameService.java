package services;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
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
                return new JoinGameResponse("Error: unauthorized", false);

            // Verify user exists
            User user = userDAO.getUser(authToken.getUsername());
            if (user == null)
                return new JoinGameResponse("Error: user not found", false);

            // Verify the game exists
            if (gameDAO.findGameById(request.getGameID()) == null)
                return new JoinGameResponse("Error: bad request", false);

            // Add player to game or watch status based on color
            if (request.getPlayerColor().equalsIgnoreCase("WHITE"))
                gameDAO.claimSpot(request.getGameID(), user.getUsername(), ChessGame.TeamColor.WHITE);
            else if (request.getPlayerColor().equalsIgnoreCase("BLACK"))
                gameDAO.claimSpot(request.getGameID(), user.getUsername(), ChessGame.TeamColor.BLACK);
            else // Watching
                return new JoinGameResponse("Successfully watching the game", true);

            return new JoinGameResponse("Successfully joined the game", true);

        } catch (DataAccessException e) {
            // Handle exceptions/errors
            if (e.getMessage().contains("already taken"))
                return new JoinGameResponse("Error: already taken", false);
            else if (e.getMessage().contains("not found"))
                return new JoinGameResponse("Error: bad request", false);
            else
                return new JoinGameResponse("Error: " + e.getMessage(), false);
        }
    }
}
