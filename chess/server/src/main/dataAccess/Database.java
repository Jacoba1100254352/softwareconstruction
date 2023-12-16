package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsible for creating connections to the database. Connections should be closed after use, either by calling
 * {@link #closeConnection(Connection)} on the Database instance or directly on the connection.
 */
public class Database {

    // mysql -u root -pnyvceB-gysvuq-gozne5

    private static final String DB_NAME = "chessServerDB";
    private static final String DB_USERNAME = "root"; // System.getProperty("dbUsername", "root"); // Fallback to 'root' if not set
    private static final String DB_PASSWORD = "nyvceB-gysvuq-gozne5"; // System.getProperty("dbPassword", ""); // Fallback to empty if not set
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static Database instance;

    /**
     * Get a Database instance
     *
     * @return Database instance
     */
    public static synchronized Database getInstance() {
        if (instance == null)
            instance = new Database();

        return instance;
    }

    /**
     * Attempts to roll back the connection transaction.
     *
     * @param conn The connection to perform the rollback on.
     * @param e    The SQLException that caused the rollback to occur.
     * @throws DataAccessException if the rollback fails.
     */
    public void rollback(Connection conn, SQLException e) throws DataAccessException {
        try {
            if (conn != null && !conn.isClosed())
                conn.rollback();
        } catch (SQLException ex) {
            throw new DataAccessException("Could not roll back transaction. Initial Exception: " + e.getMessage() + "\nAdditional Exception: " + ex.getMessage());
        }
    }

    /**
     * High-level function using the DAO clear methods to reset the database.
     *
     * @throws DataAccessException if the database reset fails.
     */
    public void resetDatabase() throws DataAccessException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection(); // Get the connection
            conn.setAutoCommit(false); // Ensure the entire operation is atomic

            stmt = conn.createStatement();

            // Disable foreign key checks for this session
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");

            // Clear all tables that have foreign key relationships
            new AuthDAO().clearAuth(conn); // Clear AuthTokens first
            new GameDAO().clearGames(conn); // Clear Games (if they have foreign keys to Users)
            new UserDAO().clearUsers(conn); // Finally, clear Users

            // Re-enable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

            conn.commit(); // Commit the changes if all operations were successful

        } catch (SQLException e) {
            try {
                conn.rollback(); // Roll back the changes on error
            } catch (SQLException ex) {
                throw new DataAccessException("Rollback failed: " + ex.getMessage());
            }
            throw new DataAccessException("Error resetting database: " + e.getMessage());
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) { /* ignored */ }
            closeConnection(conn); // Close the connection
        }
    }


    /**
     * Start a transaction.
     *
     * @throws DataAccessException if a data access error occurs.
     */
    public void startTransaction(Connection conn) throws DataAccessException {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessException("Start transaction failed: " + e.getMessage());
        }
    }

    /**
     * Gets a connection to the database.
     *
     * @return Connection the connection.
     * @throws DataAccessException if a data access error occurs.
     */
    public Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(CONNECTION_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("Error connecting to the database: " + e.getMessage());
        }
    }

    /**
     * Closes the specified connection.
     *
     * @param conn the connection to be closed.
     * @throws DataAccessException if a data access error occurs while closing the connection.
     */
    public void closeConnection(Connection conn) throws DataAccessException {
        if (conn != null) {
            try {
                if (!conn.isClosed())
                    conn.close();

            } catch (SQLException e) {
                throw new DataAccessException("Error encountered while closing the connection: " + e.getMessage());
            }
        }
    }
}
