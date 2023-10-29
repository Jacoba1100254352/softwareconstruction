package handlers;

import responses.ClearResponse;
import services.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        ClearService clearService = new ClearService();
        ClearResponse result = clearService.clearDatabase();

        if (result.isSuccess()) {
            response.status(200);
        } else {
            response.status(500);
        }
        return result;
    }
}
