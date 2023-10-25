package services;

import models.Game;
import java.util.List;

/**
 * Represents the response containing a list of all games.
 */
public class ListGamesResponse {

    /**
     * A list of games.
     */
    private List<Game> games;

    /**
     * Default constructor.
     */
    public ListGamesResponse() {}

    /**
     * Constructs a new ListGamesResponse with the given list of games.
     *
     * @param games A list of games.
     */
    public ListGamesResponse(List<Game> games) {
        this.games = games;
    }


    ///   Getters and setters   ///

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
