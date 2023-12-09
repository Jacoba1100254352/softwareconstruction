package handlers.httpHandlers;

import handlers.BaseHandler;
import requests.httpRequests.LoginRequest;
import responses.httpResponses.LoginResponse;
import services.httpServices.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
        LoginService loginService = new LoginService();
        LoginResponse result = loginService.login(loginRequest);

        if (result.isSuccess())
            response.status(200);
        else
            response.status(result.getMessage().equals("Error: unauthorized") ? 401 : 500);

        return result;
    }
}