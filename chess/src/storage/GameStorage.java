package storage;

import models.Game;

import java.util.Collection;
import java.util.HashMap;

public class GameStorage {
    private final HashMap<Integer, Game> games;
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

    public Collection<Game> getAllGames() {
        return games.values();
    }

    public void clearGames() {
        games.clear();
    }

    public Integer getNextGameId() {
        return ++idCounter;
    }
}
