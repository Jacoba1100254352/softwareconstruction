import handlers.*;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.HashMap;

public class Server {
    private final HashMap<String, BaseHandler> handlers;

    public Server() {
        handlers = new HashMap<>();

        // Using compound keys (path + method) with separators for handlers
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
    }

    public String handleRequest(Request req, Response res) {
        String key = req.pathInfo() + ":" + req.requestMethod();
        BaseHandler handler = handlers.get(key);

        if (handler != null) {
            try {
                Object result = handler.handleRequest(req, res); // Updated to pass Spark's request and response
                res.type("application/json"); // Set response type to JSON
                return BaseHandler.gson.toJson(result); // Convert the result object to JSON string
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
}
