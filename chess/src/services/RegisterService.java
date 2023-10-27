package services;

import models.User;
import storage.StorageManager;
import storage.UserStorage;

import java.util.UUID;

/**
 * Provides services for registering a user.
 */
public class RegisterService {
    /**
     * In-memory storage for the users.
     */
    UserStorage users = StorageManager.getInstance().getUserStorage();

    /**
     * Registers a new user.
     *
     * @param request The registration request containing user details.
     * @return RegisterResponse indicating success or failure.
     */
    public RegisterResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty() ||
                request.getEmail() == null || request.getEmail().isEmpty()) {
            return new RegisterResponse("Error: bad request");
        }

        if (!users.getUsers().containsKey(request.getUsername())) {
            User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
            users.getUsers().put(request.getUsername(), newUser);

            String uniqueToken = UUID.randomUUID().toString();

            return new RegisterResponse(uniqueToken, request.getUsername());
        } else {
            return new RegisterResponse("Error: already taken");
        }
    }
}
