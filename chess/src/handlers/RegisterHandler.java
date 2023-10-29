package handlers;

import requests.RegisterRequest;
import responses.RegisterResponse;
import services.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        RegisterResponse result = registerService.register(registerRequest);

        if (result.getMessage() != null) {
            switch (result.getMessage()) {
                case "Error: bad request":
                    response.status(400);
                    break;
                case "Error: already taken":
                    response.status(403);
                    break;
                default:
                    response.status(500);
                    break;
            }
        } else {
            response.status(200);
        }
        return result;
    }
}
