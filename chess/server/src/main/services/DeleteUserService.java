package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import requests.DeleteUserRequest;
import responses.DeleteUserResponse;

import java.sql.SQLException;

/**
 * Provides services to delete a specific user.
 */
public class DeleteUserService {
    /**
     * Deletes a specific user from the database.
     *
     * @param request The request containing the authToken and the username of the user to be deleted.
     * @return DeleteUserResponse indicating success or failure.
     */
    public DeleteUserResponse deleteUser(DeleteUserRequest request) {
        UserDAO userDao = new UserDAO();
        AuthDAO authDao = new AuthDAO();

        try {
            // Retrieve the authToken and check for null
            AuthToken authToken = authDao.findAuth(request.getAuthToken());
            if (authToken == null) {
                return new DeleteUserResponse("Invalid or expired authToken provided.", false);
            }

            // Retrieve the user associated with the authToken
            User requestingUser = userDao.getUser(authToken.getUsername());
            if (requestingUser == null) {
                return new DeleteUserResponse("User associated with authToken not found.", false);
            }

            // Check if the requesting user is the same as the user to be deleted or is an admin
            if (!requestingUser.getUsername().equals(request.getUsername()) && !requestingUser.getIsAdmin()) {
                return new DeleteUserResponse("Unauthorized: You do not have permission to delete this user.", false);
            }

            // Delete the user
            userDao.deleteUser(request.getUsername());
            return new DeleteUserResponse("User deleted successfully.", true);
        } catch (DataAccessException | SQLException e) {
            return new DeleteUserResponse("Database error: " + e.getMessage(), false);
        }
    }
}
