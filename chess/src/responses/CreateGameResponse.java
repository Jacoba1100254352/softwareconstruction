package responses;

/**
 * Represents the response after attempting to create a game.
 */
public class CreateGameResponse {

    /**
     * The unique ID of the created game.
     */
    private Integer gameID;

    /**
     * A message providing success or error info.
     */
    private String message;


    ///   Constructors   ///

    /**
     * Constructor for the response gameID.
     *
     * @param gameID The unique ID of the created game.
     */
    public CreateGameResponse(Integer gameID) {
        this.gameID = gameID;
    }

    /**
     * Constructor for the response message.
     *
     * @param message A message providing success or error info.
     */
    public CreateGameResponse(String message) {
        this.message = message;
    }


    ///   Getters and setters   ///

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
