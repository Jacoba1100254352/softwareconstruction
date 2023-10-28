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

        if (result == null) {
            response.status(500);
            return new CreateGameResponse("Error: unexpected error occurred.");
        } else if (result.getGameID() == null) {
            if ("Error: bad request".equals(result.getMessage())) {
                response.status(400);
            } else if ("Error: unauthorized".equals(result.getMessage())) {
                response.status(401);
            } else {
                response.status(500);
            }
            return result;
        }

        response.status(200);
        return result;
    }
}
