package handlers;

import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private final Map<Session, String> userSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        System.out.println("New WebSocket connection: " + session.getRemoteAddress().getAddress());
        userSessions.put(session, null);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket closed: " + session.getRemoteAddress().getAddress() + ", Code: " + statusCode + ", Reason: " + reason);
        userSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        processMessage(session, message);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
        userSessions.remove(session);
    }

    private void processMessage(Session session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            handleCommand(session, command);
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }

    private void handleCommand(Session session, UserGameCommand command) {
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                handleJoinPlayer(session, (JoinPlayerCommand) command);
                break;
            case JOIN_OBSERVER:
                handleJoinObserver(session, (JoinObserverCommand) command);
                break;
            case MAKE_MOVE:
                handleMakeMove(session, (MakeMoveCommand) command);
                break;
            case LEAVE:
                handleLeave(session, (LeaveCommand) command);
                break;
            case RESIGN:
                handleResign(session, (ResignCommand) command);
                break;
            default:
                sendErrorMessage(session, "Unknown command type");
                break;
        }
    }

    private void handleJoinPlayer(Session session, JoinPlayerCommand command) {
        // Example pseudocode
        Game game = findGameById(command.getGameID());
        if (game == null) {
            sendErrorMessage(session, "Game not found");
            return;
        }
        boolean joined = game.addPlayer(command.getPlayerColor(), command.getAuthToken());
        if (!joined) {
            sendErrorMessage(session, "Cannot join as requested color");
            return;
        }
        notifyClients(game, "Player joined: " + command.getPlayerColor());
    }

    private void handleJoinObserver(Session session, JoinObserverCommand command) {
        Game game = findGameById(command.getGameID());
        if (game == null) {
            sendErrorMessage(session, "Game not found");
            return;
        }
        game.addObserver(session);
        notifyClients(game, "New observer joined");
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        Game game = findGameById(command.getGameID());
        if (game == null || !game.isPlayerTurn(command.getAuthToken())) {
            sendErrorMessage(session, "Invalid move or not your turn");
            return;
        }
        if (!game.makeMove(command.getMove())) {
            sendErrorMessage(session, "Illegal move");
            return;
        }
        notifyClients(game, "Move made: " + command.getMove());
    }

    private void handleLeave(Session session, LeaveCommand command) {
        Game game = findGameById(command.getGameID());
        if (game == null) {
            sendErrorMessage(session, "Game not found");
            return;
        }
        game.removeParticipant(session);
        notifyClients(game, "User left the game");
    }

    private void handleResign(Session session, ResignCommand command) {
        Game game = findGameById(command.getGameID());
        if (game == null) {
            sendErrorMessage(session, "Game not found");
            return;
        }
        game.markAsResigned(command.getAuthToken());
        notifyClients(game, "Player has resigned");
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ServerMessage errorMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            String jsonErrorMsg = gson.toJson(errorMsg);
            session.getRemote().sendString(jsonErrorMsg);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
