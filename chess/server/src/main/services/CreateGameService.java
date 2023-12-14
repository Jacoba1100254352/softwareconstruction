package services;

import chess.ChessGame;
import chess.ChessGameImpl;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.Game;
import requests.CreateGameRequest;
import responses.CreateGameResponse;

/**
 * Provides services to create a new game.
 */
public class CreateGameService {
    private final GameDAO gameDAO = new GameDAO();
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * Creates a new game based on the provided request.
     *
     * @param request The request containing game details.
     * @return CreateGameResponse with the operation's result.
     */
    public CreateGameResponse createGame(CreateGameRequest request) {
        try {
            if (request.gameName() == null || request.gameName().isEmpty())
                return new CreateGameResponse("Error: bad request");

            if (authDAO.findAuth(request.authToken()) == null)
                return new CreateGameResponse("Error: unauthorized");

            // Create a new Game object
            ChessGame game = new ChessGameImpl();
            game.getBoard().resetBoard();
            Integer gameID = (!gameDAO.findAllGames().isEmpty()) ? gameDAO.getCurrentGameId() : 0;
            Game newGame = new Game(gameID, request.gameName(), game);

            // Insert the new Game object into the data store
            gameDAO.insertGame(newGame);

            return new CreateGameResponse(newGame.getGameID());
        } catch (DataAccessException e) {
            return new CreateGameResponse("Error: " + e.getMessage());
        }
    }
}
