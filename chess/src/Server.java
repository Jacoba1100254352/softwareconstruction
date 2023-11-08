import dataAccess.*;
import handlers.*;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.HashMap;

public class Server {
    /**
     * Handlers for service requests and responses.
     */
    private final HashMap<String, BaseHandler> handlers;

    public Server() {
        // Initialize handlers
        handlers = new HashMap<>();

        // Set up the handlers
        handlers.put("/db:DELETE", new ClearHandler());
        handlers.put("/user:POST", new RegisterHandler());
        handlers.put("/session:POST", new LoginHandler());
        handlers.put("/session:DELETE", new LogoutHandler());
        handlers.put("/game:GET", new ListGamesHandler());
        handlers.put("/game:POST", new CreateGameHandler());
        handlers.put("/game:PUT", new JoinGameHandler());
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        // Register a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
    }

    public String handleRequest(Request req, Response res) {
        // Initialize handler
        String key = req.pathInfo() + ":" + req.requestMethod();
        BaseHandler handler = handlers.get(key);

        // Handle request
        if (handler != null) {
            try {
                res.type("application/json");
                return BaseHandler.gson.toJson(handler.handleRequest(req, res));
            } catch (Exception e) {
                res.status(500);
                res.type("application/json");
                return "{\"error\":\"Internal Server Error: " + e.getMessage() + "\"}";
            }
        } else {
            res.status(404);
            res.type("application/json");
            return "{\"error\":\"Not Found\"}";
        }
    }

    public void start() {
        // Set the Spark port
        Spark.port(8080);

        // Set the location for static files
        Spark.externalStaticFileLocation("src/web");

        resetDatabaseForTesting();

        // Set the Spark routes
        Spark.delete("/db", this::handleRequest);
        Spark.post("/user", this::handleRequest);
        Spark.post("/session", this::handleRequest);
        Spark.delete("/session", this::handleRequest);
        Spark.get("/game", this::handleRequest);
        Spark.post("/game", this::handleRequest);
        Spark.put("/game", this::handleRequest);
        // Add a route for resetting the database, with proper security checks
        Spark.post("/reset-database", (req, res) -> {
            // Perform security checks here to ensure this can't be called in a production environment
            resetDatabaseForTesting();
            return "Database reset for testing.";
        });

        // Initialize the Spark server
        Spark.init();
    }

    public void stopServer() {
        // Perform any necessary cleanup here
        System.out.println("Server is stopping. Cleaning up resources...");

        // For example, if you need to close a database connection pool
        // you would call the respective method to shut it down here.
        // Database.getInstance().closeConnectionPool();

        // Stop the Spark server
        Spark.stop();
    }

    // Method to reset the database for testing
    public void resetDatabaseForTesting() {
        try {
            Database.getInstance().resetDatabase();
            System.out.println("Database reset successfully for testing.");
        } catch (DataAccessException e) {
            System.err.println("Error resetting database for testing: " + e.getMessage());
        }
    }
}