package storage;

import models.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStorage {
    private final Map<Integer, Game> games;
    private Integer idCounter;

    public GameStorage() {
        games = new HashMap<>();
        idCounter = 0;
    }

    public boolean containsGame(Integer gameID) {
        return games.containsKey(gameID);
    }

    public Game getGame(Integer gameID) {
        return games.get(gameID);
    }

    public void addGame(Game game) {
        games.put(game.getGameID(), game);
    }

    public void deleteGame(Integer gameID) {
        games.remove(gameID);
    }

    public List<Game> getAllGames() {
        return new ArrayList<>(games.values());
    }

    public void clearGames() { games.clear(); }

    public Integer getNextGameId() {
        return ++idCounter;
    }
}
