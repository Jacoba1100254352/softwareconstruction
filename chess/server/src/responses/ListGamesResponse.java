package responses;

import models.Game;

import java.util.Collection;

/**
 * Represents the response containing a list of all games.
 */
public class ListGamesResponse {

    /**
     * A list of games.
     */
    private Collection<Game> games;
    /**
     * A message providing details or an error description.
     */
    private String message;
    /**
     * The game listing was successful
     */
    private boolean success;


    ///   Constructors   ///

    /**
     * Constructor for creating list of games after a successful operation.
     *
     * @param games A list of games.
     */
    public ListGamesResponse(Collection<Game> games) {
        this.games = games;
        this.success = true;
    }

    /**
     * Constructor for the list game response success or failure.
     *
     * @param success Indicates the success of the List Game Response.
     * @param message A message providing details or an error description.
     */
    public ListGamesResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    ///   Getters and setters   ///

    public Collection<Game> getGames() {
        return games;
    }

    public void setGames(Collection<Game> games) {
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
