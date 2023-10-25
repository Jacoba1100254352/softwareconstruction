package dataAccess;

import models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for handling user-related data operations.
 */
public class UserDAO {

    /**
     * In-memory storage for users
     */
    private final Map<String, User> userMap;

    /**
     * Default constructor.
     */
    public UserDAO() {
        userMap = new HashMap<>();
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object containing user details.
     * @throws DataAccessException if there's an error during insertion.
     */
    public void insertUser(User user) throws DataAccessException {
        if (userMap.containsKey(user.getUsername())) {
            throw new DataAccessException("User with this username already exists.");
        }
        userMap.put(user.getUsername(), user);
    }

    /**
     * Retrieves a user based on username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, (null otherwise).
     * @throws DataAccessException if there's an error during retrieval.
     */
    public User getUser(String username) throws DataAccessException {
        return userMap.get(username);
    }

    /**
     * Updates a user's details in the database.
     *
     * @param user The updated User object.
     * @throws DataAccessException if there's an error during update or user doesn't exist.
     */
    public void updateUser(User user) throws DataAccessException {
        if (!userMap.containsKey(user.getUsername()))
            throw new DataAccessException("User not found.");
        userMap.put(user.getUsername(), user);
    }

    /**
     * Deletes a user from the database.
     *
     * @param username The username of the user to delete.
     * @throws DataAccessException if there's an error during deletion or user doesn't exist.
     */
    public void deleteUser(String username) throws DataAccessException {
        if (!userMap.containsKey(username))
            throw new DataAccessException("User not found.");
        userMap.remove(username);
    }
}
