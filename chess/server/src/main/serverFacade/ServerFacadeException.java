package serverFacade;

// Custom exception class
public class ServerFacadeException extends Exception {
    public ServerFacadeException(String message, Throwable cause) {
        super(message, cause);
    }
}