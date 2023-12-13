package webSocketManagement;

import chess.*;
import com.google.gson.Gson;
import dataAccess.*;
import models.AuthToken;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

@WebSocket
public class WebSocketHandler {
    private final GameDAO gameDAO;
    private final ConnectionManager connectionManager;
    private final ObserverManager observerManager;
    private final MessageDispatcher messageDispatcher;
    private final Gson gson;

    public WebSocketHandler() {
        gameDAO = new GameDAO();
        connectionManager = new ConnectionManager();
        observerManager = new ObserverManager();
        messageDispatcher = new MessageDispatcher(connectionManager, observerManager);
        //gson = new GsonBuilder().registerTypeAdapter(ChessPosition.class, new ChessPosAdapter()).registerTypeAdapter(ChessMove.class, new ChessMoveAdapter()).create();
        gson = new Gson();// new GsonBuilder().registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter()).create();
    }

    // Handle incoming WebSocket messages
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        // Deserialize the incoming message to a UserGameCommand
        UserGameCommand gameCmd = gson.fromJson(message, UserGameCommand.class);

        // Handle different command types
        switch (gameCmd.getCommandType()) {
            case JOIN_OBSERVER -> handleJoinObserver(session, gson.fromJson(message, JoinObserverCommand.class));
            case JOIN_PLAYER -> handleJoinPlayer(session, gson.fromJson(message, JoinPlayerCommand.class));
            case MAKE_MOVE -> handleMove(session, gson.fromJson(message, MakeMoveCommand.class));
            case RESIGN -> handleResign(session, gson.fromJson(message, ResignCommand.class));
            case LEAVE -> handleLeave(session, gson.fromJson(message, LeaveCommand.class));
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        // Here, handle session removal when a connection is closed
        System.out.println("Closed: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket Error: " + throwable.getMessage());
    }

    private void handleJoinObserver(Session session, JoinObserverCommand gameCmd) throws Exception {
        Integer gameID = gameCmd.getGameID();
        Game game = gameDAO.findGameByID(gameID);

        // Find the game by ID and handle cases where the game doesn't exist
        if (game == null || game.getGameName() == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error 403: Bad request, game id not in database"); // "Error 404: Game not found"
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Find the username from the auth token
        String userName = findUser(session, gameCmd.getAuthString());
        if (userName == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error 401: Unauthorized");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Check if the user is already an observer
        if (!observerManager.isObserver(userName, gameID)) {
            // Add the user as an observer if they are not already observing
            observerManager.add(gameID, new ObserverInstance(session, userName));
            connectionManager.add(gameID, new ConnectionInstance(session, userName));

            // Send game state to the new observer first
            if (game.getGame() != null) {
                LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
                messageDispatcher.sendMessage(session, loadGameMessage);
            } else {
                ErrorMessage errorMessage = new ErrorMessage("Error: Unable to load game data");
                messageDispatcher.sendMessage(session, errorMessage);
            }

            // Prepare and broadcast notification to all players and observers in the game, except the new observer
            NotificationMessage notification = new NotificationMessage(userName + " now observing");
            messageDispatcher.broadcastToGame(gameCmd.getGameID(), notification, session);
        } else {
            // Handle the case where the user is already an observer
            ErrorMessage errorMessage = new ErrorMessage(userName + " is already observing game " + gameID);
            messageDispatcher.sendMessage(session, errorMessage);
        }
    }

    /**
     * Handles a player's request to join a game.
     * @param session The WebSocket session for the client.
     * @param gameCmd The command containing details about the join request.
     * @throws Exception If there's an error in processing the request.
     */
    private void handleJoinPlayer(Session session, JoinPlayerCommand gameCmd) throws Exception {
        // Retrieve the player color and game ID from the command
        ChessGame.TeamColor color = gameCmd.getPlayerColor();
        Integer gameID = gameCmd.getGameID();

        // Access the game from the database
        GameDAO gameDao = new GameDAO();
        Game game = gameDao.findGameByID(gameID);

        // Check if the game is valid
        if (game == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error 404: Game not found");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Determine the current player for the requested color
        String userInGame = (color.equals(ChessGame.TeamColor.WHITE)) ? game.getWhiteUsername() : game.getBlackUsername();

        // Identify the user from the session
        String userName = findUser(session, gameCmd.getAuthString());
        if (userName == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error 401: Unauthorized");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Check if the color is already taken
        if (!userName.equals(userInGame)) {
            ErrorMessage errorMessage = new ErrorMessage("Error 403: Color already taken");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Check if there are already two players in the game
        if (game.getWhiteUsername() != null && game.getBlackUsername() != null) {
            // Add user as an observer instead
            handleJoinObserverAutomatically(session, gameID, userName);
            return;
        }

        // Set the turn to the joining player's color if it's WHITE
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            game.getGame().setTeamTurn(ChessGame.TeamColor.WHITE);
        }

        // Update the game with the new player
        connectionManager.add(gameCmd.getGameID(), new ConnectionInstance(session, userName));
        gameDao.updateGame(game);

        // After the player joins successfully:
        NotificationMessage notification = new NotificationMessage(userName + " joined as " + color);
        messageDispatcher.broadcastToGameExcept(gameCmd.getGameID(), notification, userName);

        if (game.getGame() != null) {
            LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
            messageDispatcher.sendMessage(session, loadGameMessage);
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Error: Unable to load game data");
            messageDispatcher.sendMessage(session, errorMessage);
        }
    }

    // New method to automatically handle joining as an observer
    private void handleJoinObserverAutomatically(Session session, Integer gameID, String userName) throws Exception {
        if (!observerManager.isObserver(userName, gameID)) {
            observerManager.add(gameID, new ObserverInstance(session, userName));
            connectionManager.add(gameID, new ConnectionInstance(session, userName));

            if (gameDAO.findGameByID(gameID).getGame() != null) {
                LoadGameMessage loadGameMessage = new LoadGameMessage(gameDAO.findGameByID(gameID).getGame());
                messageDispatcher.sendMessage(session, loadGameMessage);
            } else {
                ErrorMessage errorMessage = new ErrorMessage("Error: Unable to load game data");
                messageDispatcher.sendMessage(session, errorMessage);
            }

            NotificationMessage notification = new NotificationMessage(userName + " now observing");
            messageDispatcher.broadcastToGame(gameID, notification, session);
        } else {
            ErrorMessage errorMessage = new ErrorMessage(userName + " is already observing game " + gameID);
            messageDispatcher.sendMessage(session, errorMessage);
        }
    }

    /**
     * Handles a player's move request.
     * @param session The WebSocket session for the client.
     * @param gameCmd The command containing details about the move.
     * @throws Exception If there's an error in processing the request.
     */
    private void handleMove(Session session, MakeMoveCommand gameCmd) throws Exception {
        // Identify the user from the session
        String userName = findUser(session, gameCmd.getAuthString());
        if (userName == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error 401: Unauthorized");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Access the game from the database
        GameDAO gameDao = new GameDAO();
        Game game = gameDao.findGameByID(gameCmd.getGameID());
        if (game == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error 404: Game not found");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Check if the game is over
        if (game.getGame().getTeamTurn() == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Cannot make a move, game already concluded");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Determine the player's color in the game
        ChessGame.TeamColor color = (game.getWhiteUsername().equals(userName)) ? ChessGame.TeamColor.WHITE : game.getBlackUsername().equals(userName) ? ChessGame.TeamColor.BLACK : null;

        // Check if the player can make a move
        if (!canPlayerMove(session, game, color, userName)) {
            return; // Error messages are sent within the canPlayerMove method
        }

        // Process the move
        processPlayerMove(session, gameCmd, game, userName);

        // Prepare and send updated game state to all clients in the game
        LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
        messageDispatcher.broadcastToGame(gameCmd.getGameID(), loadGameMessage, session);

        // Prepare and broadcast move notification
        String moveDescription = gameCmd.getMove().toString(); // Format as needed
        NotificationMessage notification = new NotificationMessage(userName + " made a move: " + moveDescription);
        messageDispatcher.broadcastToGame(gameCmd.getGameID(), notification, session);
    }

    /**
     * Checks if the player can make a move and sends appropriate error messages if not.
     * @param session The WebSocket session for the client.
     * @param game The game object.
     * @param color The color of the player.
     * @param userName The username of the player.
     * @return True if the player can move, false otherwise.
     */
    private boolean canPlayerMove(Session session, Game game, ChessGame.TeamColor color, String userName) {
        // Check if the user is an observer, or it's not their turn
        if (color == null || !color.equals(game.getGame().getTeamTurn())) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid move"); // "Error: Not your turn"
            messageDispatcher.sendMessage(session, errorMessage);
            return false;
        }

        // Check if the user is connected
        if (!connectionManager.getSessionsFromGame(game.getGameID()).containsKey(userName)) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Not connected");
            messageDispatcher.sendMessage(session, errorMessage);
            return false;
        }

        // Check if the game is over
        if (game.getGame().getTeamTurn() == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Game over");
            messageDispatcher.sendMessage(session, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * Processes the player's move in the game.
     * @param session The WebSocket session for the client.
     * @param gameCmd The move command.
     * @param game The game object.
     * @param userName The username of the player.
     * @throws Exception If there's an error in processing the move.
     */
    private void processPlayerMove(Session session, MakeMoveCommand gameCmd, Game game, String userName) throws Exception {
        // Attempt to make the move
        try {
            game.getGame().makeMove(gameCmd.getMove());
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Update the game state in the database
        gameDAO.updateGame(game);

        // Notify all clients about the move
        sendMoveNotification(game, userName, gameCmd.getMove());
        checkForCheckAndCheckmate(game);

        // Send updated game state to all clients in the game
        LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
        messageDispatcher.broadcastToGame(gameCmd.getGameID(), loadGameMessage, session);

        // Send a notification about the move to all clients
        NotificationMessage notification = new NotificationMessage(userName + " made a move: " + gameCmd.getMove());
        messageDispatcher.broadcastToGame(gameCmd.getGameID(), notification, session);
    }

    /**
     * Sends a notification about the player's move to all connected clients.
     * @param game The game object.
     * @param userName The username of the player who made the move.
     * @param move The move that was made.
     */
    private void sendMoveNotification(Game game, String userName, ChessMove move) {
        NotificationMessage notification = new NotificationMessage(userName + " moved " + move);
        messageDispatcher.broadcastToGameExcept(game.getGameID(), notification, userName);
    }

    /**
     * Checks for check and checkmate situations and sends notifications if applicable.
     * @param game The game object.
     */
    private void checkForCheckAndCheckmate(Game game) throws DataAccessException {
        if (game.getGame().isInCheck(ChessGame.TeamColor.WHITE)) {
            messageDispatcher.broadcastToAll(new NotificationMessage(game.getWhiteUsername() + " is in check!"));
        }
        if (game.getGame().isInCheck(ChessGame.TeamColor.BLACK)) {
            messageDispatcher.broadcastToAll(new NotificationMessage(game.getBlackUsername() + " is in check!"));
        }
        if (game.getGame().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            game.getGame().setTeamTurn(null);
            gameDAO.updateGame(game);
            messageDispatcher.broadcastToGameExcept(game.getGameID(), new NotificationMessage(game.getWhiteUsername() + " got checkmated!"), null);
        }
        if (game.getGame().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            game.getGame().setTeamTurn(null);
            gameDAO.updateGame(game);
            messageDispatcher.broadcastToGameExcept(game.getGameID(), new NotificationMessage(game.getBlackUsername() + " got checkmated!"), null);
        }
    }

    /**
     * Handles a player's resignation from a game.
     * @param session The WebSocket session for the client.
     * @param gameCmd The "resign" command received from the client.
     * @throws Exception If an error occurs during the process.
     */
    private void handleResign(Session session, ResignCommand gameCmd) throws Exception {
        GameDAO gameDao = new GameDAO();
        Game game = gameDao.findGameByID(gameCmd.getGameID());
        String userName = findUser(session, gameCmd.getAuthString());

        if (userName == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Check if the user is an observer
        if (observerManager.isObserver(userName, gameCmd.getGameID())) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Observer can't resign");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Check if the game is already concluded
        if (game.getGame().getTeamTurn() == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Cannot resign after game conclusion");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Set the game's turn to null to indicate a game end
        game.getGame().setTeamTurn(null);
        gameDao.updateGame(game);

        // Send a notification to all users about the resignation
        NotificationMessage notification = new NotificationMessage(userName + " resigned");
        messageDispatcher.broadcastToAll(notification);
    }

    /**
     * Handles a player leaving a game.
     * @param session The WebSocket session for the client.
     * @param gameCmd The leave command received from the client.
     * @throws Exception If an error occurs during the process.
     */
    private void handleLeave(Session session, LeaveCommand gameCmd) throws Exception {
        String userName = findUser(session, gameCmd.getAuthString());
        Game game = gameDAO.findGameByID(gameCmd.getGameID());

        // Check if the game is already concluded
        if (game.getGame().getTeamTurn() == null) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Cannot resign, game already concluded");
            messageDispatcher.sendMessage(session, errorMessage);
            return;
        }

        // Determine the color of the player leaving
        ChessGame.TeamColor color = determinePlayerColor(game, userName);

        if (color == null) {
            observerManager.remove(userName, gameCmd.getGameID()); // Remove as observer
        } else {
            gameDAO.claimSpot(gameCmd.getGameID(), null, color); // Remove from player position
        }

        connectionManager.remove(userName, gameCmd.getGameID()); // Remove from connection manager

        // Send a notification about the player leaving the game
        NotificationMessage notification = new NotificationMessage(userName + " left the game");
        messageDispatcher.broadcastToAll(notification);
    }

    /**
     * Determines the color of a player in a game.
     * @param game The game object.
     * @param userName The username of the player.
     * @return The color of the player, or null if the player is not found.
     */
    private ChessGame.TeamColor determinePlayerColor(Game game, String userName) {
        if (userName.equals(game.getWhiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (userName.equals(game.getBlackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    /**
     * Finds the username associated with a given authentication token.
     * @param session The WebSocket session for the client.
     * @param authToken The authentication token provided by the client.
     * @return The username corresponding to the authentication token.
     * @throws Exception If the token is invalid or an error occurs.
     */
    private String findUser(Session session, String authToken) throws Exception {
        try {
            AuthToken auth = new AuthDAO().findAuth(authToken);
            return auth.getUsername();
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage("Error 401: unauthorized. Exception: " + e.getMessage());
            messageDispatcher.sendMessage(session, errorMessage);
            throw new Exception("Error 401: unauthorized");
        }
    }
}