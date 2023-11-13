package handlers;

import requests.LoginRequest;
import responses.LoginResponse;
import services.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
        LoginService loginService = new LoginService();
        LoginResponse result = loginService.login(loginRequest);

        if (!result.isSuccess())
            response.status(result.getMessage().equals("Error: unauthorized") ? 401 : 500);
        else response.status(200);

        return result;
    }
}