package services;


import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import models.AuthToken;
import requests.LogoutRequest;
import responses.LogoutResponse;


/**
 * Provides services to log out a user.
 */
public class LogoutService
{
	private final AuthDAO authDAO = new AuthDAO();
	
	/**
	 * Logs out a user based on the provided request.
	 *
	 * @param request The logout request with the user's authToken.
	 *
	 * @return LogoutResponse indicating success or failure.
	 */
	public LogoutResponse logout(LogoutRequest request) {
		try {
			AuthToken authToken = authDAO.findAuth(request.authToken());
			if (authToken != null) {
				authDAO.deleteAuth(authToken);
				return new LogoutResponse("Logged out successfully.", true);
			} else {
				return new LogoutResponse("Error: Invalid authentication token.", false);
			}
		} catch (DataAccessException e) {
			return new LogoutResponse("Error: " + e.getMessage(), false);
		}
	}
}
