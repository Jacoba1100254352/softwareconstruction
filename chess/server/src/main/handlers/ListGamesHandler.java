package handlers;

import requests.ListGamesRequest;
import responses.ListGamesResponse;
import services.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        String authToken = request.headers("Authorization");
        ListGamesResponse result = (new ListGamesService()).listAllGames(new ListGamesRequest(authToken));

        if (result.success())
            response.status(200);
        else
            response.status(("Error: unauthorized".equals(result.message())) ? 401 : 500);

        return result;
    }
}
