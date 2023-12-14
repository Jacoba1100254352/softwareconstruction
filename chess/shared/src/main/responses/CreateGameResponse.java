package responses;

/**
 * Represents the response after attempting to create a game.
 */
public class CreateGameResponse implements Response {
    /**
     * The unique ID of the created game.
     */
    private final Integer gameID;

    /**
     * A message providing success or error info.
     */
    private final String message;

    /**
     * Indicates the success of the create game operation.
     */
    private final boolean success;


    ///   Constructors   ///

    /**
     * Constructor for the response gameID.
     *
     * @param gameID The unique ID of the created game.
     */
    public CreateGameResponse(Integer gameID) {
        this.gameID = gameID;
        this.message = null;
        this.success = true;
    }

    /**
     * Constructor for the response message.
     *
     * @param message A message providing error info.
     */
    public CreateGameResponse(String message) {
        this.gameID = null;
        this.message = message;
        this.success = false;
    }


    ///   Getters and setters   ///

    public Integer getGameID() {
        return gameID;
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
