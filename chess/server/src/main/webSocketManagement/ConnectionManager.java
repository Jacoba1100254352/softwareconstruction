package webSocketManagement;

import org.eclipse.jetty.websocket.api.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConnectionManager implements ClientManager {
    private final ConcurrentHashMap<String, ConnectionInstance> connections;
    private final ConcurrentHashMap<Integer, Set<ConnectionInstance>> gameSessions;

    public ConnectionManager() {
        connections = new ConcurrentHashMap<>();
        gameSessions = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Integer gameID, ClientInstance instance) {
        if (instance instanceof ConnectionInstance connectionInstance) {
            connections.put(connectionInstance.getUsername(), connectionInstance);
            gameSessions.computeIfAbsent(gameID, k -> new HashSet<>()).add(connectionInstance);
        }
    }

    @Override
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
                sessions.put(instance.getUsername(), instance.getSession());
            }
        }
        return sessions;
    }

    public Collection<Session> getAllSessions() {
        return connections.values().stream().map(ConnectionInstance::getSession).collect(Collectors.toList());
    }

}
