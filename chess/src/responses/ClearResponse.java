package responses;

/**
 * Represents the response after attempting to clear the database.
 */
public class ClearResponse {

    /**
     * Indicates the success of the clear operation.
     */
    private boolean success;

    /**
     * A message providing success or error info.
     */
    private String message;


    ///   Constructor   ///

    /**
     * Constructor for the clear response success or failure.
     *
     * @param success Indicates the success of the clear operation.
     * @param message A message providing success or error info.
     */
    public ClearResponse(boolean success, String message) {
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
