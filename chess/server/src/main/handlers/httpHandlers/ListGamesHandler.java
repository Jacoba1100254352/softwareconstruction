package handlers.httpHandlers;

import handlers.BaseHandler;
import requests.httpRequests.ListGamesRequest;
import responses.httpResponses.ListGamesResponse;
import services.httpServices.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        String authToken = request.headers("Authorization");
        ListGamesResponse result = (new ListGamesService()).listAllGames(new ListGamesRequest(authToken));

        if (result.isSuccess())
            response.status(200);
        else
            response.status(("Error: unauthorized".equals(result.getMessage())) ? 401 : 500);

        return result;
    }
}
