package services;

/**
 * Represents the response after attempting to create a game.
 */
public class CreateGameResponse {

    /**
     * The unique ID of the created game.
     */
    private int gameID;

    /**
     * Default constructor.
     */
    public CreateGameResponse() {}

    /**
     * Constructs a new CreateGameResponse with the given gameID.
     *
     * @param gameID The unique ID of the created game.
     */
    public CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }


    ///   Getters and setters   ///

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
