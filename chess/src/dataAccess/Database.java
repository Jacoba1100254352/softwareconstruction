package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsible for creating connections to the database. Connections should be closed after use, either by calling
 * {@link #closeConnection(Connection)} on the Database instance or directly on the connection.
 */
public class Database {

    private static final String DB_NAME = "chessServerDB";
    private static final String DB_USERNAME = "root"; //System.getProperty("dbUsername", "root"); // Fallback to 'root' if not set
    private static final String DB_PASSWORD = "nyvceB-gysvuq-gozne5"; // System.getProperty("dbPassword", ""); // Fallback to empty if not set

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;

    private static Database instance;

    private Database() {
        // Private constructor to prevent instantiation
    }

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
     * @param connection the connection to be closed.
     * @throws DataAccessException if a data access error occurs while closing the connection.
     */
    public void closeConnection(Connection connection) throws DataAccessException {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }
}
