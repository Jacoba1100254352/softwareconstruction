package services;

import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");
        ClearRequest clearRequest = new ClearRequest(authToken);
        ClearService clearService = new ClearService();
        ClearResponse result = clearService.clearDatabase(clearRequest);

        if (result.isSuccess()) {
            response.status(200);
        } else {
            response.status(500);
        }
        return result;
    }
}
