package responses;

/**
 * Represents the response to the logout request.
 */
public class LogoutResponse {

    /**
     * Indicates the success of the logout operation.
     */
    private boolean success;

    /**
     * A message providing success or error info.
     */
    private String message;


    ///   Constructors   ///

    /**
     * Constructor for the logout response success or failure.
     *
     * @param success Indicates if the logout operation was successful.
     * @param message A message providing success or error info.
     */
    public LogoutResponse(boolean success, String message) {
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
