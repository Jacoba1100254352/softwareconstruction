package handlers;

import services.ClearService;

import requests.ClearRequest;
import responses.ClearResponse;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        ClearService clearService = new ClearService();
        ClearResponse result = clearService.clearDatabase(new ClearRequest(authToken));

        if (result.isSuccess())
            response.status(200);
        else
            response.status(500);

        return result;
    }
}
