package dataAccess;


import models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;


/**
 * Responsible for handling user-related data operations.
 */
public class UserDAO
{
	
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
	 *
	 * @throws DataAccessException if there's an error during insertion.
	 */
	public void insertUser(User user) throws DataAccessException {
		// Updated SQL query to include the isAdmin field
		String sql = "INSERT INTO Users (Username, Password, Email, IsAdmin) VALUES (?, ?, ?, ?);";
		Connection conn = null;
		try {
			conn = db.getConnection();
			db.startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, user.getUsername());
				stmt.setString(2, hashPassword(user.getPassword()));
				stmt.setString(3, user.getEmail());
				stmt.setBoolean(4, user.getIsAdmin());
				
				stmt.executeUpdate();
				conn.commit();
			} catch (SQLException e) {
				db.rollback(conn, e);
				throw new DataAccessException("Error encountered while inserting user " + user.getUsername() + ": " + e.getMessage());
			}
		} finally {
			db.closeConnection(conn);
		}
	}
	
	/**
	 * Hash the password for security.
	 *
	 * @param password The password to hash.
	 *
	 * @return The new hashed password.
	 *
	 * @throws DataAccessException if there's an error during the password hashing.
	 */
	public String hashPassword(String password) throws DataAccessException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hashedPassword);
		} catch (NoSuchAlgorithmException e) {
			throw new DataAccessException("Could not hash password: " + e.getMessage());
		}
	}
	
	/**
	 * Validates the user's password using the hash stored in the database.
	 *
	 * @param username The username for which the password validation is to be performed.
	 * @param password The plain text password to validate.
	 *
	 * @return true if the provided password matches the stored hash, false otherwise.
	 *
	 * @throws DataAccessException If there is an issue accessing the database or performing the query.
	 */
	public boolean validatePassword(String username, String password) throws DataAccessException {
		String sql = "SELECT Password FROM Users WHERE Username = ?;";
		try (
				Connection conn = db.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)
		) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			
			return rs.next() && rs.getString("Password").equals(hashPassword(password));
		} catch (SQLException e) {
			throw new DataAccessException("Error encountered while validating user password: " + e.getMessage());
		}
	}
	
	/**
	 * Retrieves a user based on username.
	 *
	 * @param username The username of the user to retrieve.
	 *
	 * @return The User object if found, (null otherwise).
	 *
	 * @throws DataAccessException if there's an error during retrieval.
	 */
	public User getUser(String username) throws DataAccessException {
		String sql = "SELECT * FROM Users WHERE Username = ?;";
		
		try (
				Connection conn = db.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)
		) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			
			return (rs.next()) ? new User(rs.getString("Username"), rs.getString("Password"), rs.getString("Email"), rs.getBoolean("IsAdmin")) : null;
		} catch (SQLException e) {
			throw new DataAccessException("Error encountered while finding user: " + e.getMessage());
		}
	}
	
	/**
	 * Updates a user's details in the database.
	 *
	 * @param user The updated User object.
	 *
	 * @throws DataAccessException if there's an error during update or user doesn't exist.
	 */
	public void updateUser(User user) throws DataAccessException {
		String sql = "UPDATE Users SET Password = ?, Email = ?, IsAdmin = ? WHERE Username = ?;";
		
		try (
				Connection conn = db.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)
		) {
			stmt.setString(1, user.getPassword());
			stmt.setString(2, user.getEmail());
			stmt.setBoolean(3, user.getIsAdmin());
			stmt.setString(4, user.getUsername());
			
			if (stmt.executeUpdate() != 1) throw new DataAccessException("User update failed: User not found.");
		} catch (SQLException e) {
			throw new DataAccessException("Error encountered while updating the database: " + e.getMessage());
		}
	}
	
	/**
	 * Deletes a user from the database.
	 *
	 * @param username The username of the user to delete.
	 *
	 * @throws DataAccessException if there's an error during deletion or user doesn't exist.
	 * @throws SQLException        if there is an error during the sql set auto commit process.
	 */
	public void deleteUser(String username) throws DataAccessException, SQLException {
		String sql = "DELETE FROM Users WHERE Username = ?;";
		Connection conn = null;
		try {
			conn = db.getConnection();
			conn.setAutoCommit(false); // Start transaction
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, username);
				
				if (stmt.executeUpdate() == 0) throw new DataAccessException("User deletion failed: User not found.");
				
				conn.commit(); // Commit transaction
			} catch (SQLException e) {
				db.rollback(conn, e);
				throw new DataAccessException("Error encountered while deleting user: " + e.getMessage());
			}
		} finally {
			db.closeConnection(conn);
		}
	}
	
	public void clearUsers(Connection conn) throws DataAccessException {
		String sql = "DELETE FROM Users WHERE IsAdmin = false;"; // Delete all except admin // "DELETE FROM Users;";
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DataAccessException("Error encountered while clearing users: " + e.getMessage());
		}
	}
}
