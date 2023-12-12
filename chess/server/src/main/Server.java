import handlers.*;
import spark.Request;
import spark.Response;
import spark.Spark;
import testFactory.TestFactory;
import webSocketManagement.WebSocketHandler;

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
        setupHandlers();
    }

    private void setupHandlers() {
        handlers.put("/db:DELETE", new ClearHandler());
        handlers.put("/user:POST", new RegisterHandler());
        handlers.put("/session:POST", new LoginHandler());
        handlers.put("/session:DELETE", new LogoutHandler());
        handlers.put("/game:GET", new ListGamesHandler());
        handlers.put("/game:POST", new CreateGameHandler());
        handlers.put("/game:PUT", new JoinGameHandler());
    }

    public static void main(String[] args) {
        // Setup server instance
        Server server = new Server();

        // Start the server
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
                return "{\"error\":\"Internal server error: " + e.getMessage() + "\"}";
            }
        } else {
            res.status(404);
            res.type("application/json");
            return "{\"error\":\"Endpoint " + req.pathInfo() + " not found\"}";
        }
    }

    public void start() {
        // Set the Spark port
        Spark.port(Integer.parseInt(TestFactory.getServerPort()));

        Spark.webSocket("/connect", WebSocketHandler.class);

        // Set security headers
        Spark.before((request, response) -> {
            response.header("Content-Security-Policy", "default-src 'self'");
            response.header("X-Content-Type-Options", "nosniff");
            response.header("X-Frame-Options", "SAMEORIGIN");
            response.header("X-XSS-Protection", "1; mode=block");
        });

        // Basic rate limiting
        /*Spark.before((request, response) -> {
            String ip = request.ip();
            int count = requestCounts.getOrDefault(ip, 0);
            if (count > 100) { // Limit of 100 requests per IP, adjust as needed
                Spark.halt(429, "Too many requests");
            }
            requestCounts.put(ip, count + 1);
        });*/

        // Set the Spark routes
        Spark.delete("/db", this::handleRequest);
        Spark.post("/user", this::handleRequest);
        Spark.post("/session", this::handleRequest);
        Spark.delete("/session", this::handleRequest);
        Spark.get("/game", this::handleRequest);
        Spark.post("/game", this::handleRequest);
        Spark.put("/game", this::handleRequest);

        // Initialize the Spark server
        Spark.init();
    }

    public void stopServer() {
        try {
            // TODO: Perform necessary shutdown logic
            // For example, gracefully close WebSocket connections, release resources, etc.

            // Stop the Spark server
            Spark.stop();

            // Additional logic for shutting down other components, if necessary

            System.out.println("Server stopped successfully.");
        } catch (Exception e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
        }
    }
}