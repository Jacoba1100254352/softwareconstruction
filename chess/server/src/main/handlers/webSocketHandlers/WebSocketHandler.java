package handlers.webSocketHandlers;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import sessions.WebSocketSessions;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.userCommands.*;

public class WebSocketHandler {
    private final WebSocketSessions sessions;
    private final Gson gson = new Gson();

    public WebSocketHandler(WebSocketSessions sessions) {
        this.sessions = sessions;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection: " + session.getRemoteAddress().getAddress());
        sessions.addSessionToGame(null, null, session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        processMessage(session, message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket closed: " + statusCode + ", Reason: " + reason);
        sessions.remove(session);
    }

    private void processMessage(Session session, String message) {
        try {
            switch (gson.fromJson(message, UserGameCommand.class).getCommandType()) {
                case JOIN_PLAYER -> handleJoinPlayer(session, gson.fromJson(message, JoinPlayerCommand.class));
                case JOIN_OBSERVER -> handleJoinObserver(session, gson.fromJson(message, JoinObserverCommand.class));
                case MAKE_MOVE -> handleMakeMove(session, gson.fromJson(message, MakeMoveCommand.class));
                case LEAVE -> handleLeave(session, gson.fromJson(message, LeaveCommand.class));
                case RESIGN -> handleResign(session, gson.fromJson(message, ResignCommand.class));
                default -> sendErrorMessage(session, "Unknown command type");
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }

    // call service and send messages to clients
    private void handleJoinPlayer(Session session, JoinPlayerCommand command) {

    }

    private void handleJoinObserver(Session session, JoinObserverCommand command) {
        // gameService.method(.)
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        // sendMessage(...)
    }

    private void handleLeave(Session session, LeaveCommand command) {
        // broadcastMessage(…)
    }

    private void handleResign(Session session, ResignCommand command) {

    }

    private void sendErrorMessage(Session session, String message) {
        sendErrorMessage(session, "Database error: " + e.getMessage());
    }

    private void sendMessage(Integer gameID, String message, String username) {
        sessions.forEach((session, user) -> {
            if (user != null && user.equals(username)) {
                sendMessage(session, message);
            }
        });
    }

    private void broadcastMessage(Object message) {
        sessions.forEach((session, user) -> sendMessage(session, message));
    }
}
