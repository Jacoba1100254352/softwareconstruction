package services;

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
            response.status(400);
            return result;
        }

        response.status(200);
        return result;
    }
}
