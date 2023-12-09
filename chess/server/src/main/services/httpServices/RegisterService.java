package services.httpServices;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import requests.httpRequests.RegisterRequest;
import responses.httpResponses.RegisterResponse;

import java.util.UUID;

/**
 * Provides services for registering a user.
 */
public class RegisterService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

    /**
     * Registers a new user.
     *
     * @param request The registration request containing user details.
     * @return RegisterResponse indicating success or failure.
     */
    public RegisterResponse register(RegisterRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isEmpty() ||
                    request.getPassword() == null || request.getPassword().isEmpty() ||
                    request.getEmail() == null || request.getEmail().isEmpty()) {
                return new RegisterResponse("Error: bad request");
            }

            if (userDAO.getUser(request.getUsername()) == null) {
                userDAO.insertUser(new User(request.getUsername(), request.getPassword(), request.getEmail()));

                String uniqueToken = UUID.randomUUID().toString();
                authDAO.insertAuth(new AuthToken(uniqueToken, request.getUsername()));

                return new RegisterResponse(uniqueToken, request.getUsername());
            } else {
                return new RegisterResponse("Error: already taken");
            }
        } catch (DataAccessException e) {
            return new RegisterResponse("Error: " + e.getMessage());
        }
    }
}
