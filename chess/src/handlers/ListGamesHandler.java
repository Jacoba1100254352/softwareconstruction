package handlers;

import requests.ListGamesRequest;
import responses.ListGamesResponse;
import services.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");
        ListGamesResponse result = (new ListGamesService()).listAllGames(new ListGamesRequest(authToken));

        if (!result.isSuccess()) {
            if ("Error: unauthorized".equals(result.getMessage()))
                response.status(401);
            else
                response.status(500);
        } else
            response.status(200);
        return result;
    }
}
