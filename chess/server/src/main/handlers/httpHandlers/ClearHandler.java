package handlers.httpHandlers;

import handlers.BaseHandler;
import requests.httpRequests.ClearRequest;
import services.httpServices.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        ClearService clearService = new ClearService();
        responses.Response result = clearService.clearDatabase(new ClearRequest(authToken));

        response.status(result.isSuccess() ? 200 : 500);

        return result;
    }
}
