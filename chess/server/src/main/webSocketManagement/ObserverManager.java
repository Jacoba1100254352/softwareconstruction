package webSocketManagement;

import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

public class ObserverManager {
    private final Map<Integer, Set<Session>> gameObservers;

    public ObserverManager() {
        gameObservers = new HashMap<>();
    }

    public void addObserver(Integer gameId, Session session) {
        gameObservers.computeIfAbsent(gameId, k -> new HashSet<>()).add(session);
    }

    public void removeObserver(Integer gameId, Session session) {
        Set<Session> observers = gameObservers.get(gameId);
        if (observers != null) {
            observers.remove(session);
            if (observers.isEmpty()) {
                gameObservers.remove(gameId);
            }
        }
    }

    public Set<Session> getObservers(Integer gameId) {
        return gameObservers.getOrDefault(gameId, Collections.emptySet());
    }
}

