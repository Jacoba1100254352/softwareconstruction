package handlers;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final Map<Session, String> userSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New WebSocket connection: " + session.getRemoteAddress().getAddress());
        userSessions.put(session, null);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket closed: " + statusCode + ", Reason: " + reason);
        userSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message received: " + message);
        processMessage(session, message);
    }

    private void processMessage(Session session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            // Determine the type of command and handle accordingly
            switch (command.getCommandType()) {
                case JOIN_PLAYER:
                    handleJoinPlayer(session, gson.fromJson(message, JoinPlayerCommand.class));
                    break;
                case JOIN_OBSERVER:
                    handleJoinObserver(session, gson.fromJson(message, JoinObserverCommand.class));
                    break;
                case MAKE_MOVE:
                    handleMakeMove(session, gson.fromJson(message, MakeMoveCommand.class));
                    break;
                // Add cases for LEAVE and RESIGN
                default:
                    sendErrorMessage(session, "Unknown command type");
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }

    private void handleJoinPlayer(Session session, JoinPlayerCommand command) {
        // TODO: Add your game logic here
        // Example: Add the player to the game with the specified ID and color
        // If successful, send a confirmation message back to the client
        // If not successful, send an error message

        // Pseudocode:
        // if (addPlayerToGame(command.getGameID(), command.getPlayerColor())) {
        //     sendMessage(session, new NotificationMessage("Player joined: " + command.getPlayerColor()));
        // } else {
        //     sendErrorMessage(session, "Could not join player to game");
        // }
    }

    private void handleJoinObserver(Session session, JoinObserverCommand command) {
        // TODO: Add your game logic for an observer joining a game
        // Similar to handleJoinPlayer, but for adding observers

        // Pseudocode:
        // if (addObserverToGame(command.getGameID())) {
        //     sendMessage(session, new NotificationMessage("Observer joined game"));
        // } else {
        //     sendErrorMessage(session, "Could not add observer to game");
        // }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) {
        // TODO: Process the move made by a player
        // Validate the move, update the game state, and notify all clients about the move

        // Pseudocode:
        // if (makeMoveInGame(command.getGameID(), command.getMove())) {
        //     broadcastToAllClients(new LoadGameMessage("Updated game state"));
        // } else {
        //     sendErrorMessage(session, "Invalid move");
        // }
    }
    // These methods should interact with your game logic and send appropriate responses.

    // TODO: Implement additional methods for LEAVE and RESIGN commands

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ErrorMessage errorMsg = new ErrorMessage(errorMessage);
            String jsonErrorMsg = gson.toJson(errorMsg);
            session.getRemote().sendString(jsonErrorMsg);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private void sendMessage(Session session, ServerMessage message) {
        try {
            String jsonMsg = gson.toJson(message);
            session.getRemote().sendString(jsonMsg);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    // Additional utility methods can be added as needed
}
