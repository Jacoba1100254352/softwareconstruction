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
        String sql = "INSERT INTO AuthTokens (Token, Username) VALUES (?, ?);";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = db.getConnection(); // Get the connection
            conn.setAutoCommit(false); // Start the transaction

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, authToken.getToken());
            stmt.setString(2, authToken.getUsername());
            stmt.executeUpdate();

            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            try {
                conn.rollback(); // Roll back the transaction on error
            } catch (SQLException ex) {
                throw new DataAccessException("Rollback failed: " + ex.getMessage());
            }
            throw new DataAccessException("Error encountered while inserting auth token: " + e.getMessage());
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignored */ }
            db.closeConnection(conn); // Close the connection in the finally block
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
        String sql = "SELECT * FROM AuthTokens WHERE Token = ?;";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                token = new AuthToken(rs.getString("Token"), rs.getString("Username"));
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
        String sql = "DELETE FROM AuthTokens WHERE Token = ?;";

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
    public void clearAuth(Connection conn) throws DataAccessException {
        String sql = "DELETE FROM AuthTokens;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing tokens: " + e.getMessage());
        }
    }
}
