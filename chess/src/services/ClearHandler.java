package services;

import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {
    @Override
    public Object handleRequest(Request request, Response response) {
        response.type("application/json");
        String authToken = request.headers("Authorization");

        // Create a ClearRequest object and set the authToken
        ClearRequest clearRequest = new ClearRequest(authToken);

        ClearService clearService = new ClearService();

        // Pass the ClearRequest object to the service method
        ClearResponse result = clearService.clearDatabase(clearRequest);

        if (result.isSuccess()) {
            response.status(200);
            return new SuccessResponse("Database cleared successfully.");
        } else {
            response.status(401); // Unauthorized
            return new ErrorResponse(result.getMessage());
        }
    }
}
