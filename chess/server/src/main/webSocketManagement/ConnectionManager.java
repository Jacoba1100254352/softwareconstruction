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

    public void sendToAll(String exceptUserName, Object notification) throws IOException {
        for (ConnectionInstance connection : connections.values()) {
            if (connection.session.isOpen() && !connection.userName.equals(exceptUserName)) {
                connection.send(new Gson().toJson(notification));
            }
        }
    }
}
