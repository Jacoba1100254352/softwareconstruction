package handlers;

import services.ListGamesService;

import requests.ListGamesRequest;
import responses.ListGamesResponse;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        String authToken = request.headers("Authorization");
        ListGamesResponse result = (new ListGamesService()).listAllGames(new ListGamesRequest(authToken));

        if (!result.isSuccess())
            response.status(("Error: unauthorized".equals(result.getMessage())) ? 401 : 500);
        else response.status(200);

        return result;
    }
}
