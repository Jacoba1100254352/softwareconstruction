package services;

import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");

        String authToken = request.headers("Authorization");

        // Create a LogoutRequest object and set the authToken
        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        LogoutService logoutService = new LogoutService();

        // Pass the LogoutRequest object to the service method
        LogoutResponse result = logoutService.logout(logoutRequest);

        if (!result.isSuccess()) {
            response.status(401); // Unauthorized
            return gson.toJson(new ErrorResponse("Error: unauthorized"));
        }

        response.status(200);
        return gson.toJson(new SuccessResponse("Logged out successfully."));

    }
}
