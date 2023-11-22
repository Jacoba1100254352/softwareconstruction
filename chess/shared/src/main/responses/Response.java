package responses;

public interface Response {
    String getMessage();

    void setMessage(String message);

    boolean isSuccess();

    void setSuccess(boolean success);
}
