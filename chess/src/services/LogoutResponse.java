package services;

/**
 * Represents the result of a logout request.
 */
public class LogoutResponse {

    /**
     * Indicates if the logout operation was successful.
     */
    private boolean success;

    /**
     * A message providing details or an error description.
     */
    private String message;

    /**
     * Default constructor.
     */
    public LogoutResponse() {}

    /**
     * Constructs a new LogoutResponse with the given parameters.
     *
     * @param success Indicates if the logout operation was successful.
     * @param message A message providing details or an error description.
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
