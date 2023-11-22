package responses;

import models.Game;

import java.util.Collection;

/**
 * Represents the response containing a list of all games.
 */
public class ListGamesResponse implements Response {

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
        this.success = true; // true: successful list games
    }

    /**
     * Constructor for the list game response success or failure.
     *
     * @param message A message providing details or an error description.
     * @param success Indicates the success of the List Game Response.
     */
    public ListGamesResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }


    ///   Getters and setters   ///

    public Collection<Game> getGames() {
        return games;
    }

    public void setGames(Collection<Game> games) {
        this.games = games;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
