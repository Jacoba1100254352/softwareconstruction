package handlers;

import requests.RegisterRequest;
import responses.RegisterResponse;
import services.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        RegisterResponse result = registerService.register(registerRequest);

        if (result.success()) {
            response.status(200);
        } else {
            switch (result.message()) {
                case "Error: bad request" -> response.status(400);
                case "Error: already taken" -> response.status(403);
                default -> response.status(500);
            }
        }

        return result;
    }
}
