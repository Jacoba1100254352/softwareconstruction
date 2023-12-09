package responses.httpResponses;

import responses.Response;

/**
 * Represents the response after attempting to create a game.
 */
public class CreateGameResponse implements Response {
    /**
     * The unique ID of the created game.
     */
    private Integer gameID;

    /**
     * A message providing success or error info.
     */
    private String message;

    /**
     * Indicates the success of the create game operation.
     */
    private boolean success;


    ///   Constructors   ///

    /**
     * Constructor for the response gameID.
     *
     * @param gameID The unique ID of the created game.
     */
    public CreateGameResponse(Integer gameID) {
        this.gameID = gameID;
        this.success = true;
    }

    /**
     * Constructor for the response message.
     *
     * @param message A message providing error info.
     */
    public CreateGameResponse(String message) {
        this.message = message;
        this.success = false;
    }


    ///   Getters and setters   ///

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
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
