package responses.webSocketResponses;

import responses.Response;

public class ResignGameResponse implements Response {

    private String message;
    private boolean success;

    public ResignGameResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }


    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void setSuccess(boolean success) {

    }
}
