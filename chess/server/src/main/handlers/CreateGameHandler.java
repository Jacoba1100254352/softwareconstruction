package handlers;

import requests.CreateGameRequest;
import responses.CreateGameResponse;
import services.CreateGameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        String authToken = request.headers("Authorization");
        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
        CreateGameResponse result = (new CreateGameService()).createGame(new CreateGameRequest(authToken, createGameRequest.gameName()));

        if (result.success()) {
            response.status(200);
        } else {
            switch (result.message()) {
                case "Error: bad request" -> response.status(400);
                case "Error: unauthorized" -> response.status(401);
                default -> response.status(500);
            }
        }

        return result;
    }
}
