import handlers.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import spark.Request;
import spark.Response;
import spark.Spark;

import org.eclipse.jetty.websocket.server.NativeWebSocketServletContainerInitializer;

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
                return "{\"error\":\"Internal server.Server Error: " + e.getMessage() + "\"}";
            }
        } else {
            res.status(404);
            res.type("application/json");
            return "{\"error\":\"Not Found\"}";
        }
    }

    public void start() {
        setupSparkServer();

        // Start WebSocket server
        setupWebSocketServer();
    }

    private void setupSparkServer() {
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

        // Initialize the Spark server
        Spark.init();
    }

    private void setupWebSocketServer() {
        int webSocketPort = 8081;
        org.eclipse.jetty.server.Server jettyServer = new org.eclipse.jetty.server.Server(webSocketPort);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        NativeWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.addMapping("/ws/*", WebSocketHandler.class);
        });

        jettyServer.setHandler(context);

        try {
            jettyServer.start();
            System.out.println("WebSocket Server started on port " + webSocketPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        // TODO: Perform necessary shutdown logic, For example, gracefully close WebSocket connections, release resources, etc.

        // Stop the Spark server
        Spark.stop();

        // Stop the Spark server
        System.out.println("Server stopped successfully.");
    }
}