import dataAccess.DataAccessException;
import handlers.*;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.HashMap;

import dataAccess.UserDAO;
import models.User;

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
        handlers.put("/user/:username:DELETE", new DeleteUserHandler()); // Admin: Delete specific user
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
                return "{\"error\":\"Internal server.Server Error: " + e.getMessage() + "\"}";
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

        // Set the Spark routes
        Spark.delete("/db", this::handleRequest);
        Spark.post("/user", this::handleRequest);
        Spark.post("/session", this::handleRequest);
        Spark.delete("/session", this::handleRequest);
        Spark.get("/game", this::handleRequest);
        Spark.post("/game", this::handleRequest);
        Spark.put("/game", this::handleRequest);
        Spark.delete("/user/:username", this::handleRequest); // Admin: Delete specific user

        createAdminAccount(); // Ensure admin account exists

        // Initialize the Spark server
        Spark.init();
    }

    public void stopServer() {
        // Stop the Spark server
        Spark.stop();

        // Stop the Spark server
        System.out.println("server.Server stopped successfully.");
    }

    private void createAdminAccount() {
        UserDAO userDAO = new UserDAO();
        try {
            String adminUsername = "admin"; // Admin username
            String adminPassword = "adminPassword"; // Admin password
            String adminEmail = "admin@example.com"; // Admin email

            // Check if admin already exists
            if (userDAO.getUser(adminUsername) == null)
                userDAO.insertUser(new User(adminUsername, adminPassword, adminEmail, true));

        } catch (DataAccessException e) {
            System.err.println("Error creating admin account: " + e.getMessage());
        }
    }
}
