package responses;

/**
 * Represents the response after attempting to clear the database.
 */
public class DeleteUserResponse implements Response {
    /**
     * A message providing success or error info.
     */
    private String message;

    /**
     * Indicates the success of the clear operation.
     */
    private boolean success;


    ///   Constructor   ///

    /**
     * Constructor for the clear response success or failure.
     *
     * @param message A message providing success or error info.
     * @param success Indicates the success of the clear operation.
     */
    public DeleteUserResponse(String message, boolean success) {
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