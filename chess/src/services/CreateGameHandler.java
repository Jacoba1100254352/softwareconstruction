package services;

import spark.Request;
import spark.Response;

public class CreateGameHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");
        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
        createGameRequest.setAuthToken(authToken);

        CreateGameService createGameService = new CreateGameService();
        CreateGameResponse result = createGameService.createGame(createGameRequest);

        if (result == null || result.getGameID() == 0) {
            response.status(400);
            return new ErrorResponse("Invalid authentication token or game creation failed.");
        }

        response.status(200);
        return result;
    }
}
