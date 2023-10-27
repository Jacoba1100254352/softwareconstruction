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
     * A message providing details or an error description.
     */
    private String message;
    /**
     * The game listing was successful
     */
    private boolean success = true;  // Default to true. Set to false on errors.

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
