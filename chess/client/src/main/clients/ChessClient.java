package clients;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import serverFacade.ServerFacade;
import testFactory.TestFactory;
import ui.*;
import WebSocketFacade.WebSocketFacade;
import webSocketMessages.serverMessages.*;

public class ChessClient {
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private final WebSocketFacade webSocketFacade;
    private boolean isPlayer;
    private boolean canMove;
    private Integer curID; // Current game ID
    private String authToken;

    private String clientUsername; // Username of the current user
    private boolean isAdmin;
    private boolean isRunning;
    private boolean isLoggedIn;

    public ChessClient() {
        ServerFacade serverFacade = new ServerFacade("http://localhost:" + TestFactory.getServerPort());

        this.webSocketFacade = new WebSocketFacade(this);
        preloginUI = new PreloginUI(this, serverFacade);
        postloginUI = new PostloginUI(this, serverFacade);
        gameplayUI = new GameplayUI(this);
        isRunning = true;
        isPlayer = false;
        canMove = false;
    }

    public void run() {
        while (isRunning) {
            if (isLoggedIn) {
                postloginUI.displayMenu();
            } else {
                preloginUI.displayMenu();
            }
        }
    }

    public void resignGame() throws Exception {
        if (!isPlayer) {
            throw new Exception("Must be in a game to resign");
        }
        // Assuming a message format for resigning the game
        JsonObject resignMessage = new JsonObject();
        resignMessage.addProperty("type", "resign");
        resignMessage.addProperty("gameID", curID);
        resignMessage.addProperty("authToken", authToken);

        // Send the message to the server or WebSocket
        webSocketFacade.sendMessage(resignMessage.toString());

        // Update client state
        isPlayer = false;
        canMove = false;
    }

    public void leaveGame() throws Exception {
        if (!isPlayer) {
            throw new Exception("Must be in a game to leave it");
        }
        // Assuming a message format for leaving the game
        JsonObject leaveMessage = new JsonObject();
        leaveMessage.addProperty("type", "leave");
        leaveMessage.addProperty("gameID", curID);
        leaveMessage.addProperty("authToken", authToken);

        // Send the message to the server or WebSocket
        webSocketFacade.sendMessage(leaveMessage.toString());

        // Update client state
        isPlayer = false;
        canMove = false;
    }

    // Method to make a move in the game
    public void makeMove(ChessMove move) throws Exception {
        if (!canMove) {
            throw new Exception("It's not your turn to move.");
        }

        // Create a JSON object representing the move
        JsonObject moveJson = new JsonObject();
        moveJson.addProperty("type", "move");
        moveJson.addProperty("gameID", this.curID);
        moveJson.addProperty("authToken", this.authToken);
        moveJson.add("move", new Gson().toJsonTree(move));

        // Send the move message to the server or WebSocket
        webSocketFacade.sendMessage(moveJson.toString());

        // Update the client's state if necessary
        // For example, toggle canMove to false
        this.canMove = false;
    }

    // Method to join a game as a player
    public void joinPlayer(String colorStr, Integer gameID) {

        // Create a JSON object for joining as a player
        JsonObject joinJson = new JsonObject();
        joinJson.addProperty("type", "joinPlayer");
        joinJson.addProperty("gameID", gameID);
        joinJson.addProperty("color", colorStr);
        joinJson.addProperty("authToken", this.authToken);

        // Send the join message to the server or WebSocket
        webSocketFacade.sendMessage(joinJson.toString());

        // Update the client state
        this.isPlayer = true;
        this.canMove = true; // This might be true or false depending on the game logic
        this.curID = gameID;
    }

    // Method to join a game as an observer
    public void joinObserver(Integer gameID) {
        // Create a JSON object for joining as an observer
        JsonObject joinJson = new JsonObject();
        joinJson.addProperty("type", "joinObserver");
        joinJson.addProperty("gameID", gameID);
        joinJson.addProperty("authToken", this.authToken);

        // Send the join message to the server or WebSocket
        webSocketFacade.sendMessage(joinJson.toString());

        // Update the client state
        this.isPlayer = false;
        this.canMove = false;
        this.curID = gameID;
    }

    // Method to notify the user about various types of messages
    public void notifyUser(String message) {
        if (message.contains("NOTIFICATION")) {
            // Extract and process the notification message
            NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
            System.out.println("\nNotification: " + notification.getNotificationMessage());
        } else if (message.contains("ERROR")) {
            // Extract and process the error message
            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
            System.out.println("\nError: " + error.getErrorMessage());
        } else {
            // Default handling for other types of messages
            System.out.println("\n" + message);
        }
        // Additional formatting or handling can be added as needed
    }


    public void exit() {
        isRunning = false;
    }

    public GameplayUI getGameplayUI() {
        return gameplayUI;
    }

    public void transitionToPostloginUI() {
        isLoggedIn = true;
    }

    public void transitionToPreloginUI() {
        isLoggedIn = false;
    }

    public boolean currentUserIsLoggedIn() {
        return isLoggedIn;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void drawChessboard() {
        gameplayUI.drawChessboard();
    }

    public static void main(String[] args) {
        (new ChessClient()).run();
    }
}
