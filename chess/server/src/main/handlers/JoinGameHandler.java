package handlers;

import requests.JoinGameRequest;
import responses.JoinGameResponse;
import services.JoinGameService;
import spark.Request;
import spark.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JoinGameHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        String authToken = request.headers("Authorization");

        // Parse the request body to a JsonObject
        JsonObject requestBody = JsonParser.parseString(request.body()).getAsJsonObject();

        // Extract gameID and playerColor from the request body
        Integer gameID = requestBody.has("gameID") ? requestBody.get("gameID").getAsInt() : null;
        String playerColor = requestBody.has("playerColor") ? requestBody.get("playerColor").getAsString() : "OBSERVER";

        // Construct the JoinGameRequest record
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, gameID, playerColor);

        JoinGameResponse result = (new JoinGameService()).joinGame(joinGameRequest);

        if (result.success()) {
            response.status(200);
        } else {
            switch (result.message()) {
                case "Error: bad request" -> response.status(400);
                case "Error: unauthorized" -> response.status(401);
                case "Error: already taken" -> response.status(403);
                default -> response.status(500);
            }
        }
        return result;
    }
}
