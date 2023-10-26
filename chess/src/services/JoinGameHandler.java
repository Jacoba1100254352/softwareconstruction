package services;

import spark.Request;
import spark.Response;

public class JoinGameHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        // Deserialize the JoinGameRequest from the request body
        JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);

        // Set the authToken of the JoinGameRequest object
        joinGameRequest.setAuthToken(authToken);

        JoinGameService joinGameService = new JoinGameService();
        JoinGameResponse result = joinGameService.joinGame(joinGameRequest);

        if (!result.isSuccess()) {
            response.status(400);
            return gson.toJson(result);
        }

        response.status(200);
        return gson.toJson(result);
    }
}
