package handlers;


import requests.DeleteUserRequest;
import responses.DeleteUserResponse;
import services.DeleteUserService;
import spark.Request;
import spark.Response;


public class DeleteUserHandler extends BaseHandler
{
	@Override
	public Object handleRequest(Request request, Response response) {
		response.type("application/json");

		String authToken = request.headers("Authorization");
		String username = request.params(":username");

		DeleteUserService deleteUserService = new DeleteUserService();
		DeleteUserResponse result = deleteUserService.deleteUser(new DeleteUserRequest(authToken, username));

		response.status(result.success() ? 200 : (result.message().startsWith("Unauthorized") ? 401 : 500));

		return result;
	}
}
