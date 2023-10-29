package responses;

/**
 * Represents the response after attempting to clear the database.
 */
public class ClearResponse {

    /**
     * Indicates if the clearing operation was successful.
     */
    private boolean success;

    /**
     * A message providing details or an error description.
     */
    private String message;

    /**
     * Default constructor.
     */
    public ClearResponse() {
    }

    /**
     * Constructs a new ClearResponse with the given parameters.
     *
     * @param success Indicates if the clearing operation was successful.
     * @param message A message providing details or an error description.
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
