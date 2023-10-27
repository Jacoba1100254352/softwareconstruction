package services;

import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesService listGamesService = new ListGamesService();
        ListGamesResponse result = listGamesService.listAllGames(listGamesRequest);

        if (!result.isSuccess()) {
            if ("Error: unauthorized".equals(result.getMessage())) {
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
