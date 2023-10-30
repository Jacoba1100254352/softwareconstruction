package dataAccess;

import models.User;
import storage.StorageManager;
import storage.UserStorage;

/**
 * Responsible for handling user-related data operations.
 */
public class UserDAO {

    /**
     * In-memory storage for users
     */
    private final UserStorage userStorage;

    /**
     * Default constructor.
     */
    public UserDAO() {
        userStorage = StorageManager.getInstance().getUserStorage();
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object containing user details.
     * @throws DataAccessException if there's an error during insertion.
     */
    public void insertUser(User user) throws DataAccessException {
        if (userStorage.containsUser(user.getUsername()))
            throw new DataAccessException("User with this username already exists.");

        userStorage.addUser(user);
    }

    /**
     * Retrieves a user based on username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, (null otherwise).
     * @throws DataAccessException if there's an error during retrieval.
     */
    public User getUser(String username) throws DataAccessException {
        return userStorage.getUser(username);
    }

    /**
     * Updates a user's details in the database.
     *
     * @param user The updated User object.
     * @throws DataAccessException if there's an error during update or user doesn't exist.
     */
    public void updateUser(User user) throws DataAccessException {
        if (!userStorage.containsUser(user.getUsername()))
            throw new DataAccessException("User not found.");

        userStorage.addUser(user);  // Since it's a Map, this will replace the existing user
    }

    /**
     * Deletes a user from the database.
     *
     * @param username The username of the user to delete.
     * @throws DataAccessException if there's an error during deletion or user doesn't exist.
     */
    public void deleteUser(String username) throws DataAccessException {
        if (!userStorage.containsUser(username))
            throw new DataAccessException("User not found.");

        userStorage.deleteUser(username);
    }

    public void clearUsers() throws DataAccessException {
        userStorage.clearUsers();
    }
}
