package webSocketManagement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ObserverManager implements ClientManager {
    private final ConcurrentHashMap<Integer, Set<ObserverInstance>> gameObservers;

    public ObserverManager() {
        gameObservers = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Integer gameId, ClientInstance observer) {
        if (observer instanceof ObserverInstance observerInstance) {
            gameObservers.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet())
                    .add(observerInstance);
        }
    }

    @Override
    public void remove(String username, Integer gameId) {
        Set<ObserverInstance> observers = gameObservers.get(gameId);
        if (observers != null) {
            observers.removeIf(observer -> observer.getUsername().equals(username));
            if (observers.isEmpty()) {
                gameObservers.remove(gameId);
            }
        }
    }

    public boolean isObserver(String username, Integer gameId) {
        Set<ObserverInstance> observers = gameObservers.get(gameId);
        return observers != null && observers.stream()
                .anyMatch(observer -> observer.getUsername().equals(username));
    }

    public Set<ObserverInstance> getUsersFromGame(Integer gameId) {
        return gameObservers.getOrDefault(gameId, Collections.emptySet());
    }
}
