package storage;

import models.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStorage {
    private final Map<Integer, Game> games = new HashMap<>();
    private int idCounter = 0;

    public boolean containsGame(int gameID) {
        return games.containsKey(gameID);
    }

    public Game getGame(int gameID) {
        return games.get(gameID);
    }

    public void addGame(Game game) {
        games.put(game.getGameID(), game);
    }

    public void deleteGame(int gameID) {
        games.remove(gameID);
    }

    public List<Game> getAllGames() {
        return new ArrayList<>(games.values());
    }

    public void clearGames() { games.clear(); }

    public int getNextGameId() {
        return idCounter++;
    }
}
