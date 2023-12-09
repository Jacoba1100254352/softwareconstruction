package responses;

import responses.Response;

/**
 * Represents the result of a join game request.
 */
public class JoinGameResponse implements Response {
    /**
     * A message providing details or an error description.
     */
    private String message;

    /**
     * Indicates the success of the join operation.
     */
    private boolean success;


    ///   Constructors   ///

    /**
     * Constructor for the join game response success or failure.
     *
     * @param message A message providing details or an error description.
     * @param success Indicates the success of the join operation.
     */
    public JoinGameResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }


    ///   Getters and setters   ///

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
