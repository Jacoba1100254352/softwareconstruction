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
    private final Collection<Game> games;

    /**
     * A message providing details or an error description.
     */
    private final String message;

    /**
     * The game listing was successful
     */
    private final boolean success;


    ///   Constructors   ///

    /**
     * Constructor for creating list of games after a successful operation.
     *
     * @param games A list of games.
     */
    public ListGamesResponse(Collection<Game> games) {
        this.games = games;
        this.message = null;
        this.success = true; // true: successful list games
    }

    /**
     * Constructor for the list game response success or failure.
     *
     * @param message A message providing details or an error description.
     * @param success Indicates the success of the List Game Response.
     */
    public ListGamesResponse(String message, boolean success) {
        this.games = null;
        this.message = message;
        this.success = success;
    }


    ///   Getters and setters   ///

    public Collection<Game> getGames() {
        return games;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public boolean success() {
        return success;
    }
}
