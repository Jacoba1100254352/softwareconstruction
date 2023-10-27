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

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutService logoutService = new LogoutService();
        LogoutResponse result = logoutService.logout(logoutRequest);

        if (!result.isSuccess()) {
            if (result.getMessage().equals("Error: unauthorized")) {
                response.status(401);
            } else {
                response.status(500);
            }
        } else {
            response.status(200);
        }
        return result;
    }
}
