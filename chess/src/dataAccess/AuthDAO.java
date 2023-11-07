package dataAccess;

import models.AuthToken;
import java.sql.*;

/**
 * Responsible for handling authentication-related data operations.
 */
public class AuthDAO {

    /**
     * Database storage for auth tokens and associated usernames
     */
    private final Database db;

    /**
     * Default constructor.
     */
    public AuthDAO() {
        this.db = Database.getInstance();
    }

    /**
     * Inserts a new token for a user.
     *
     * @param authToken The user's authentication token.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void insertAuth(AuthToken authToken) throws DataAccessException {
        // Adjust the SQL to include both the Token and the associated Username
        String sql = "INSERT INTO AuthToken (Token, Username) VALUES (?, ?);"; // Changed UserID to Username

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getToken());
            stmt.setString(2, authToken.getUsername()); // Bind the Username value here

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting auth token: " + e.getMessage());
        }
    }

    /**
     * Finds a user token.
     *
     * @param authToken The authentication token to be found.
     * @return Username associated with the token or null if not found.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public AuthToken findAuth(String authToken) throws DataAccessException {
        AuthToken token = null;
        String sql = "SELECT * FROM AuthToken WHERE Token = ?;";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                token = new AuthToken(rs.getString("Token"), rs.getString("UserID"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding token: " + e.getMessage());
        }

        return token;
    }

    /**
     * Removes a token.
     *
     * @param authToken The authentication token to be removed.
     * @throws DataAccessException if there's an error in the data access operation.
     */
    public void deleteAuth(AuthToken authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthToken WHERE Token = ?;";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getToken());

            if (stmt.executeUpdate() != 1) {
                throw new DataAccessException("Token deletion failed: Token not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting token: " + e.getMessage());
        }
    }

    /**
     * Clears all data from the database.
     */
    public void clearAuth() throws DataAccessException {
        String sql = "DELETE FROM AuthToken;";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing tokens: " + e.getMessage());
        }
    }
}
