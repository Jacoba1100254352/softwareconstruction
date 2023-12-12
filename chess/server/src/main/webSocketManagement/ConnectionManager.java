package webSocketManagement;

import org.eclipse.jetty.websocket.api.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConnectionManager {
    private final ConcurrentHashMap<String, ConnectionInstance> connections;
    private final ConcurrentHashMap<Integer, Set<ConnectionInstance>> gameSessions;

    public ConnectionManager() {
        connections = new ConcurrentHashMap<>();
        gameSessions = new ConcurrentHashMap<>();
    }

    public void add(String username, Session session, Integer gameID) {
        ConnectionInstance instance = new ConnectionInstance(session, username);
        connections.put(username, instance);
        gameSessions.computeIfAbsent(gameID, k -> new HashSet<>()).add(instance);
    }

    public void remove(String username, Integer gameID) {
        ConnectionInstance instance = connections.remove(username);
        if (instance != null && gameID != null) {
            Set<ConnectionInstance> gameSet = gameSessions.get(gameID);
            if (gameSet != null) {
                gameSet.remove(instance);
                if (gameSet.isEmpty()) {
                    gameSessions.remove(gameID);
                }
            }
        }
    }

    public void removeAll() {
        connections.clear();
        gameSessions.clear();
    }

    public Map<String, Session> getSessionsFromGame(Integer gameID) {
        Map<String, Session> sessions = new HashMap<>();
        Set<ConnectionInstance> gameSet = gameSessions.get(gameID);
        if (gameSet != null) {
            for (ConnectionInstance instance : gameSet) {
                sessions.put(instance.userName, instance.session);
            }
        }
        return sessions;
    }

    public Collection<Session> getAllSessions() {
        return connections.values().stream().map(ConnectionInstance::getSession).collect(Collectors.toList());
    }
}
