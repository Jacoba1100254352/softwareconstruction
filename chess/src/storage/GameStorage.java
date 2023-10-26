package storage;

import models.Game;

import java.util.HashMap;
import java.util.Map;

public class GameStorage {
    private final Map<Integer, Game> games = new HashMap<>();
    private int idCounter = 0;

    public Map<Integer, Game> getGames() {
        return games;
    }

    public int getNextGameId() {
        return idCounter++;
    }
}
