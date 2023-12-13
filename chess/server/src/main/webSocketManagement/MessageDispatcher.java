package webSocketManagement;

import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;

public class MessageDispatcher {
    private final ConnectionManager connectionManager;
    private final ObserverManager observerManager;
    private final Gson gson;

    public MessageDispatcher(ConnectionManager connectionManager, ObserverManager observerManager) {
        this.connectionManager = connectionManager;
        this.observerManager = observerManager;
        this.gson = new Gson();
    }

    public void sendMessage(Session session, ServerMessage message) {
        String jsonMessage = gson.toJson(message);
        try {
            if (session != null && session.isOpen()) {
                session.getRemote().sendString(jsonMessage);
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    public void broadcastToGame(Integer gameID, ServerMessage message, Session exceptSession) {
        String jsonMessage = gson.toJson(message);

        // Iterate through ObserverInstances, retrieve their Sessions, and send the message
        observerManager.getUsersFromGame(gameID).forEach(observerInstance -> {
            Session clientSession = observerInstance.getSession();
            if (clientSession != null && clientSession.isOpen() && (!clientSession.equals(exceptSession))) {
                try {
                    clientSession.getRemote().sendString(jsonMessage);
                } catch (IOException e) {
                    System.out.println("Error broadcasting to game: " + e.getMessage());
                }
            }
        });
    }

    public void broadcastToGameExcept(Integer gameID, ServerMessage message, String userExcluded) {
        String jsonMessage = gson.toJson(message);
        connectionManager.getSessionsFromGame(gameID).forEach((username, session) -> {
            if (!username.equals(userExcluded) && session != null && session.isOpen()) {
                try {
                    session.getRemote().sendString(jsonMessage);
                } catch (IOException e) {
                    System.out.println("Error in broadcastToGameExcept: " + e.getMessage());
                }
            }
        });
    }

    public void broadcastToAll(ServerMessage message) {
        String jsonMessage = gson.toJson(message);
        connectionManager.getAllSessions().forEach(session -> {
            if (session != null && session.isOpen()) {
                try {
                    session.getRemote().sendString(jsonMessage);
                } catch (IOException e) {
                    System.out.println("Error broadcasting to all: " + e.getMessage());
                }
            }
        });
    }
}
