package dataAccess;

import models.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.sql.*;

/**
 * Responsible for handling user-related data operations.
 */
public class UserDAO {

    /**
     * Database storage for users
     */
    private final Database db;

    /**
     * Default constructor.
     */
    public UserDAO() {
        this.db = Database.getInstance();
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object containing user details.
     * @throws DataAccessException if there's an error during insertion.
     */
    public void insertUser(User user) throws DataAccessException {
        String sql = "INSERT INTO Users (Username, Password, Email) VALUES (?, ?, ?);";
        Connection conn = null;
        try {
            conn = db.getConnection(); // Get connection
            db.startTransaction(conn); // Start the transaction
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashPassword(user.getPassword())); // Hash the password
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            try {
                conn.rollback(); // Roll back the transaction on error
            } catch (SQLException ex) {
                throw new DataAccessException("Error encountered while inserting user " + user.getUsername() + ": " + ex.getMessage());
            }
            throw new DataAccessException("Error encountered while inserting user " + user.getUsername() + ": " + e.getMessage());
        } finally {
            db.closeConnection(conn); // Close the connection in the finally block
        }
    }


    /**
     * Hash the password for security.
     *
     * @param password The password to hash.
     * @return The new hashed password.
     * @throws DataAccessException if there's an error during the password hashing.
     */
    private String hashPassword(String password) throws DataAccessException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new DataAccessException("Could not hash password: " + e.getMessage());
        }
    }

    /**
     *
     *
     * @param username
     * @param password
     * @return
     * @throws DataAccessException
     */
    public boolean validatePassword(String username, String password) throws DataAccessException {
        String sql = "SELECT Password FROM Users WHERE Username = ?;";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("Password");
                String hashOfInput = hashPassword(password);
                return storedHash.equals(hashOfInput);
            }
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while validating user password: " + e.getMessage());
        }
    }


    /**
     * Retrieves a user based on username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, (null otherwise).
     * @throws DataAccessException if there's an error during retrieval.
     */
    public User getUser(String username) throws DataAccessException {
        User user = null;
        String sql = "SELECT * FROM Users WHERE Username = ?;";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("Username"), rs.getString("Password"), rs.getString("Email"));
                // Assume User has a constructor User(String username, String password, String email)
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding user: " + e.getMessage());
        }

        return user;
    }

    /**
     * Updates a user's details in the database.
     *
     * @param user The updated User object.
     * @throws DataAccessException if there's an error during update or user doesn't exist.
     */
    public void updateUser(User user) throws DataAccessException {
        String sql = "UPDATE Users SET Password = ?, Email = ? WHERE Username = ?;";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getUsername());

            if (stmt.executeUpdate() != 1) {
                throw new DataAccessException("User update failed: User not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while updating the database: " + e.getMessage());
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param username The username of the user to delete.
     * @throws DataAccessException if there's an error during deletion or user doesn't exist.
     */
    public void deleteUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Users WHERE Username = ?;";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false); // Start transaction

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("User deletion failed: User not found.");
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                throw new DataAccessException("Rollback failed: " + ex.getMessage());
            }
            throw new DataAccessException("Error encountered while deleting user: " + e.getMessage());
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignored */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    public void clearUsers(Connection conn) throws DataAccessException {
        String sql = "DELETE FROM Users;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing users: " + e.getMessage());
        }
    }
}
