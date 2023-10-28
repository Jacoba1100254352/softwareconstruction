package services;

import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        if (authToken == null || authToken.isEmpty()) {
            response.status(400);
            return new LogoutResponse(false, "Error: Missing or empty Authorization header");
        }

        LogoutResponse result = (new LogoutService()).logout(new LogoutRequest(authToken));

        if (result.isSuccess())
            response.status(200);
        else response.status(result.getMessage().equals("Error: unauthorized") ? 500 : 401);

        return result;
    }
}
