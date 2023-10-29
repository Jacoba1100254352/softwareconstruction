package handlers;

import requests.LogoutRequest;
import responses.LogoutResponse;
import services.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        LogoutResponse result = (new LogoutService()).logout(new LogoutRequest(authToken));

        if (result.isSuccess())
            response.status(200);
        else response.status(result.getMessage().equals("Error: unauthorized") ? 500 : 401);

        return result;
    }
}
