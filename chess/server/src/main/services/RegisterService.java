package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import requests.RegisterRequest;
import responses.RegisterResponse;

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
            if (request.username() == null || request.username().isEmpty() ||
                    request.password() == null || request.password().isEmpty() ||
                    request.email() == null || request.email().isEmpty()) {
                return new RegisterResponse("Error: bad request");
            }

            if (userDAO.getUser(request.username()) == null) {
                userDAO.insertUser(new User(request.username(), request.password(), request.email()));

                String uniqueToken = UUID.randomUUID().toString();
                authDAO.insertAuth(new AuthToken(uniqueToken, request.username()));

                return new RegisterResponse(uniqueToken, request.username());
            } else {
                return new RegisterResponse("Error: already taken");
            }
        } catch (DataAccessException e) {
            return new RegisterResponse("Error: " + e.getMessage());
        }
    }
}
