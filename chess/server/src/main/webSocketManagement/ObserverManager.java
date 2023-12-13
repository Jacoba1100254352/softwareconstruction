package webSocketManagement;

import java.util.*;

public class ObserverManager implements ClientManager {
    private final Map<Integer, Set<ObserverInstance>> gameObservers;

    public ObserverManager() {
        gameObservers = new HashMap<>();
    }

    @Override
    public void add(Integer gameId, ClientInstance observer) {
        if (observer instanceof ObserverInstance) {
            gameObservers.computeIfAbsent(gameId, k -> new HashSet<>())
                    .add((ObserverInstance)observer);
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

    public Set<ObserverInstance> getUsersFromGame(Integer gameId) {
        return gameObservers.getOrDefault(gameId, Collections.emptySet());
    }
}
