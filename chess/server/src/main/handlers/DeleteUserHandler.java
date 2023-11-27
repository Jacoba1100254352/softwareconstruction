package handlers;

import requests.DeleteUserRequest;
import responses.DeleteUserResponse;
import services.DeleteUserService;
import spark.Request;
import spark.Response;

public class DeleteUserHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");

        String authToken = request.headers("Authorization");
        String username = request.params(":username");

        DeleteUserResponse result = (new DeleteUserService()).deleteUser(new DeleteUserRequest(authToken, username));

        response.status(result.isSuccess() ? 200 : (result.getMessage().startsWith("Unauthorized") ? 401 : 500));
        return result;
    }
}
