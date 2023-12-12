package webSocketManagement;

import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;

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

    public void sendMessage(Session session, Object message) {
        try {
            if (session != null && session.isOpen()) {
                String jsonMessage = gson.toJson(message);
                session.getRemote().sendString(jsonMessage);
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    public void broadcastToGame(Integer gameID, Object message, Session exceptSession) {
        String jsonMessage = gson.toJson(message);
        observerManager.getObservers(gameID).forEach(clientSession -> {
            if (clientSession != null && clientSession.isOpen() && (!clientSession.equals(exceptSession))) {
                try {
                    clientSession.getRemote().sendString(jsonMessage);
                } catch (IOException e) {
                    System.out.println("Error broadcasting to game: " + e.getMessage());
                }
            }
        });
    }

    public void broadcastMessage(Integer gameID, Object message, String userExcluded) {
        String jsonMessage = gson.toJson(message);
        connectionManager.getSessionsFromGame(gameID).forEach((username, session) -> {
            if (!username.equals(userExcluded) && session != null && session.isOpen()) {
                try {
                    session.getRemote().sendString(jsonMessage);
                } catch (IOException e) {
                    System.out.println("Error in broadcast: " + e);
                }
            }
        });
    }

    public void broadcastToAll(Object message) {
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
