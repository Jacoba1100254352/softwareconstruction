package webSocketManagement;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.*;
import models.AuthToken;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final GameDAO gameDAO;
    private final ConnectionManager connectionManager;
    private final ObserverManager observerManager;
    private final Gson gson;

    public WebSocketHandler() {
        gameDAO = new GameDAO();
        connectionManager = new ConnectionManager();
        observerManager = new ObserverManager(); // Initialize ObserverManager
        //gson = new GsonBuilder().registerTypeAdapter(ChessPosition.class, new PosAdapter()).registerTypeAdapter(ChessMove.class, new MoveAdapter()).create();
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
        Integer gameId = gameCmd.getGameID();
        Game game = gameDAO.findGameByID(gameId);

        // Find the game by ID and handle cases where the game doesn't exist
        if (game == null || game.getGameName() == null) {
            sendError(session, "Error 403: Bad request, game id not in database");
            return;
        }

        // Find the username from the auth token
        String userName = findUser(session, gameCmd.getAuthString());
        if (userName == null) {
            sendError(session, "Error 401: Unauthorized");
            return;
        }

        // Add the user as an observer if they are not already observing
        observerManager.addObserver(gameId, session);

        // Notify all users about the new observer and update the game state
        NotificationMessage notification = new NotificationMessage(userName + " now observing");

        System.out.println("User " + userName + " is being added as an observer for game " + gameId);
        System.out.println("Sending notification: " + notification.getNotificationMessage());

        connectionManager.add(userName, session);
        connectionManager.sendToAll(userName, notification);

        // Send a notification to all users about the new observer
        broadcastToGame(gameCmd.getGameID(), new NotificationMessage(userName + " now observing"));

        // Send game state to the new observer
        if (game.getGame() != null) {
            sendMessage(session, new LoadGameMessage(game.getGame()));
        } else {
            sendError(session, "Error: Unable to load game data");
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
            sendError(session, "Error 404: Game not found");
            return;
        }

        // Determine the current player for the requested color
        String userInGame = (color.equals(ChessGame.TeamColor.WHITE)) ? game.getWhiteUsername() : game.getBlackUsername();

        // Identify the user from the session
        String userName = findUser(session, gameCmd.getAuthString());
        if (userName == null) {
            sendError(session, "Error 401: Unauthorized");
            return;
        }

        // Check if the color is already taken
        if (!userName.equals(userInGame)) {
            sendError(session, "Error 403: Color already taken");
            return;
        }

        // Set the turn to the joining player's color if it's WHITE
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            game.getGame().setTeamTurn(ChessGame.TeamColor.WHITE);
        }

        // Update the game with the new player
        gameDao.updateGame(game);

        // Notify all connected clients about the new player
        NotificationMessage notification = new NotificationMessage(userName + " joined as " + color);
        connectionManager.add(userName, session);
        connectionManager.sendToAll(userName, notification);

        // Send the updated game state to the joining player
        session.getRemote().sendString(gson.toJson(new LoadGameMessage(game.getGame())));
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
            sendError(session, "Error 401: Unauthorized");
            return;
        }

        // Access the game from the database
        GameDAO gameDao = new GameDAO();
        Game game = gameDao.findGameByID(gameCmd.getGameID());
        if (game == null) {
            sendError(session, "Error 404: Game not found");
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

        // Send updated game state to all clients in the game
        LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
        broadcastToGame(gameCmd.getGameID(), loadGameMessage);

        // Send a notification about the move to all clients
        String moveDescription = gameCmd.getMove().toString(); // Format this as needed
        broadcastToGame(gameCmd.getGameID(), new NotificationMessage(userName + " made a move: " + moveDescription));
    }

    /**
     * Checks if the player can make a move and sends appropriate error messages if not.
     * @param session The WebSocket session for the client.
     * @param game The game object.
     * @param color The color of the player.
     * @param userName The username of the player.
     * @return True if the player can move, false otherwise.
     * @throws IOException If there's an error in sending the message.
     */
    private boolean canPlayerMove(Session session, Game game, ChessGame.TeamColor color, String userName) throws IOException {
        // Check if the user is an observer or it's not their turn
        if (color == null || !color.equals(game.getGame().getTeamTurn())) {
            sendError(session, "Error: Invalid move");
            return false;
        }

        // Check if the user is connected
        if (!connectionManager.connections.containsKey(userName)) {
            sendError(session, "Error: Not connected");
            return false;
        }

        // Check if the game is over
        if (game.getGame().getTeamTurn() == null) {
            sendError(session, "Error: Game over");
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
            sendError(session, "Error: " + e.getMessage());
            return;
        }

        // Update the game state in the database
        gameDAO.updateGame(game);

        // Notify all clients about the move
        sendMoveNotification(game, userName, gameCmd.getMove());
        checkForCheckAndCheckmate(game);
    }

    /**
     * Sends a notification about the player's move to all connected clients.
     * @param game The game object.
     * @param userName The username of the player who made the move.
     * @param move The move that was made.
     * @throws IOException If there's an error in sending the message.
     */
    private void sendMoveNotification(Game game, String userName, ChessMove move) throws IOException {
        NotificationMessage notification = new NotificationMessage(userName + " moved " + move);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game.getGame());
        connectionManager.sendToAll("", new Gson().toJson(loadGameMessage));
        connectionManager.sendToAll(userName, new Gson().toJson(notification));
    }

    /**
     * Checks for check and checkmate situations and sends notifications if applicable.
     * @param game The game object.
     * @throws IOException If there's an error in sending the message.
     */
    private void checkForCheckAndCheckmate(Game game) throws IOException, DataAccessException {
        if (game.getGame().isInCheck(ChessGame.TeamColor.WHITE)) {
            connectionManager.sendToAll("", new Gson().toJson(new NotificationMessage(game.getWhiteUsername() + " is in check!")));
        }
        if (game.getGame().isInCheck(ChessGame.TeamColor.BLACK)) {
            connectionManager.sendToAll("", new Gson().toJson(new NotificationMessage(game.getBlackUsername() + " is in check!")));
        }
        if (game.getGame().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            game.getGame().setTeamTurn(null);
            gameDAO.updateGame(game);
            connectionManager.sendToAll("", new Gson().toJson(new NotificationMessage(game.getWhiteUsername() + " got checkmated!")));
        }
        if (game.getGame().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            game.getGame().setTeamTurn(null);
            gameDAO.updateGame(game);
            connectionManager.sendToAll("", new Gson().toJson(new NotificationMessage(game.getBlackUsername() + " got checkmated!")));
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

        // Check if the user is an observer using ObserverManager
        if (observerManager.getObservers(gameCmd.getGameID()).contains(session)) {
            sendError(session, "Observer can't resign");
            return;
        }

        // Check if the game is already concluded
        if (game.getGame().getTeamTurn() == null) {
            sendError(session, "Cannot resign after game conclusion");
            return;
        }

        // Set the game's turn to null to indicate game end
        game.getGame().setTeamTurn(null);
        gameDao.updateGame(game);

        // Send a notification to all users about the resignation
        NotificationMessage notification = new NotificationMessage(userName + " resigned");
        connectionManager.sendToAll("", new Gson().toJson(notification));
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

        // Determine the color of the player leaving
        ChessGame.TeamColor color = determinePlayerColor(game, userName);

        if (color == null) {
            observerManager.removeObserver(gameCmd.getGameID(), session); // Remove as observer
        } else {
            gameDAO.claimSpot(gameCmd.getGameID(), null, color); // Remove from player position
        }

        connectionManager.remove(userName); // Remove from connection manager
        NotificationMessage notification = new NotificationMessage(userName + " left the game");
        connectionManager.sendToAll(userName, new Gson().toJson(notification));
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
        }
        return null;
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
            sendError(session, "Error 401: unauthorized");
            throw new Exception("Error 401: unauthorized");
        }
    }

    // Method to broadcast a message to all clients in a game
    private void broadcastToGame(int gameId, Object message) {
        String jsonMessage = new Gson().toJson(message);
        observerManager.getObservers(gameId).forEach(clientSession -> {
            try {
                clientSession.getRemote().sendString(jsonMessage);
            } catch (IOException e) {
                System.out.println("Error broadcasting to game: " + e.getMessage());
            }
        });
    }

    // Improved method to send a targeted message
    private void sendMessage(Session session, Object message) {
        try {
            String jsonMessage = gson.toJson(message);
            session.getRemote().sendString(jsonMessage);
        } catch (IOException e) {
            System.out.println("Send message error: " + e.getMessage());
        }
    }

    // Improved error sending method
    private void sendError(Session session, String errorMessage) {
        sendMessage(session, new ErrorMessage(errorMessage));
    }
}