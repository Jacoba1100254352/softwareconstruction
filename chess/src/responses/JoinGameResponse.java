package responses;

/**
 * Represents the result of a join game request.
 */
public class JoinGameResponse {
    /**
     * Indicates the success of the join operation.
     */
    private boolean success;
    /**
     * A message providing details or an error description.
     */
    private String message;


    ///   Constructors   ///

    /**
     * Constructor for the join game response success or failure.
     *
     * @param success Indicates the success of the join operation.
     * @param message A message providing details or an error description.
     */
    public JoinGameResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    ///   Getters and setters   ///

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
