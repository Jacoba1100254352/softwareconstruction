package services;

        import spark.Request;
        import spark.Response;
        import spark.Spark;

        import java.util.HashMap;
        import java.util.Map;

public class Server {
    private final Map<String, BaseHandler> handlers;

    public Server() {
        handlers = new HashMap<>();

        // Using compound keys (path + method) for handlers
        handlers.put("/dbDELETE", new ClearHandler());
        handlers.put("/userPOST", new RegisterHandler());
        handlers.put("/sessionPOST", new LoginHandler());
        handlers.put("/sessionDELETE", new LogoutHandler());
        handlers.put("/gameGET", new ListGamesHandler());
        handlers.put("/gamePOST", new CreateGameHandler());
        handlers.put("/gamePUT", new JoinGameHandler());
    }

    public String handleRequest(Request req, Response res) {
        String key = req.pathInfo() + req.requestMethod();
        BaseHandler handler = handlers.get(key);

        if (handler != null) {
            try {
                Object result = handler.handleRequest(req, res); // Updated to pass Spark's request and response
                return BaseHandler.gson.toJson(result); // Convert the result object to JSON string
            } catch (Exception e) {
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
        } else {
            res.status(404);
            return "Not Found";
        }
    }

    public void start() {
        // Set the port first
        Spark.port(8080);

        // Then set the location for static files (this path should point to your web directory)
        Spark.externalStaticFileLocation("src/web");

        // Now, define the Spark routes
        Spark.delete("/db", this::handleRequest);
        Spark.post("/user", this::handleRequest);
        Spark.post("/session", this::handleRequest);
        Spark.delete("/session", this::handleRequest);
        Spark.get("/game", this::handleRequest);
        Spark.post("/game", this::handleRequest);
        Spark.put("/game", this::handleRequest);

        // Finally, initialize the Spark server
        Spark.init();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}


