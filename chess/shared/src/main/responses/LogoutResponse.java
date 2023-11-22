package responses;

/**
 * Represents the response to the logout request.
 */
public class LogoutResponse implements Response {
    /**
     * A message providing success or error info.
     */
    private String message;

    /**
     * Indicates the success of the logout operation.
     */
    private boolean success;


    ///   Constructors   ///

    /**
     * Constructor for the logout response success or failure.
     *
     * @param message A message providing success or error info.
     * @param success Indicates if the logout operation was successful.
     */
    public LogoutResponse(String message, boolean success) {
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
