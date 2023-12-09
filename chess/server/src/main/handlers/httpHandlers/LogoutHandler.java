package handlers.httpHandlers;

import handlers.BaseHandler;
import requests.httpRequests.LogoutRequest;
import responses.httpResponses.LogoutResponse;
import services.httpServices.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        String authToken = request.headers("Authorization");
        LogoutResponse result = (new LogoutService()).logout(new LogoutRequest(authToken));

        if (result.isSuccess())
            response.status(200);
        else
            response.status(result.getMessage().equals("Error: unauthorized") ? 500 : 401);

        return result;
    }
}
