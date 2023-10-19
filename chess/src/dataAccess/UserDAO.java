package dataAccess;

import models.User;

/**
 * Responsible for handling user-related data operations.
 */
public class UserDAO {

    /**
     * Default constructor.
     */
    public UserDAO() {

    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object containing user details.
     * @throws DataAccessException if there's an error during insertion.
     */
    public void insertUser(User user) throws DataAccessException {

    }

    /**
     * Retrieves a user based on username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, (null otherwise).
     * @throws DataAccessException if there's an error during retrieval.
     */
    public User getUser(String username) throws DataAccessException {
        return null;
    }

    /**
     * Updates a user's details in the database.
     *
     * @param user The updated User object.
     * @throws DataAccessException if there's an error during update or user doesn't exist.
     */
    public void updateUser(User user) throws DataAccessException {

    }

    /**
     * Deletes a user from the database.
     *
     * @param username The username of the user to delete.
     * @throws DataAccessException if there's an error during deletion or user doesn't exist.
     */
    public void deleteUser(String username) throws DataAccessException {

    }
}
