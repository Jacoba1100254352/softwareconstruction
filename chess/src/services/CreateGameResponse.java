package services;

/**
 * Represents the response after attempting to create a game.
 */
public class CreateGameResponse {

    /**
     * The unique ID of the created game.
     */
    private Integer gameID;

    /**
     * A message providing details or an error description.
     */
    private String message;

    /**
     * Default constructor.
     */
    public CreateGameResponse() {}

    /**
     * Constructs a new CreateGameResponse with the given gameID.
     *
     * @param message The unique ID of the created game.
     */
    public CreateGameResponse(String message) {
        this.message = message;
    }

    /**
     * Constructs a new CreateGameResponse with the given gameID.
     *
     * @param gameID The unique ID of the created game.
     */
    public CreateGameResponse(Integer gameID) {
        this.gameID = gameID;
    }


    ///   Getters and setters   ///

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    /**
     * Retrieves the message associated with the operation.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the operation.
     *
     * @param message The message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
