package webSocketManagement;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // Concurrent to be thread safe
    public final ConcurrentHashMap<String, ConnectionInstance> connections;

    public ConnectionManager() {
        connections = new ConcurrentHashMap<>();
    }

    public void add(String username, Session session) {
        connections.put(username, new ConnectionInstance(session, username));
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void removeAll() {
        connections.clear();
    }

    public void sendToAll(String exceptUserName, Object notification) {
        String jsonMessage = new Gson().toJson(notification);
        for (ConnectionInstance connection : connections.values()) {
            try {
                if (connection.session.isOpen() && !connection.userName.equals(exceptUserName)) {
                    connection.send(jsonMessage);
                }
            } catch (IOException e) {
                System.out.println("Error sending message to " + connection.userName + ": " + e.getMessage());
                // Optionally remove the session if it's not open anymore
                if (!connection.session.isOpen()) {
                    remove(connection.userName);
                }
            }
        }
    }
}
