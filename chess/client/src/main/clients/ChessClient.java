package clients;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.Game;
import serverFacade.ServerFacade;
import ui.*;
import WebSocketFacade.WebSocketFacade;
import webSocketMessages.serverMessages.*;

import java.util.logging.Logger;

public class ChessClient {
    private final Logger LOGGER = Logger.getLogger(ChessClient.class.getName());
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private final WebSocketFacade webSocketFacade;
    private boolean playing = false;
    private boolean canMove = false;
    private Integer curID; // Current game ID
    private String currentPlayer; // Add a field to store the current player
    private String authToken;
    private String currentUsername; // Username of the current user
    private boolean isAdmin;
    private boolean isRunning;
    private boolean isLoggedIn;

    public ChessClient() {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

        this.webSocketFacade = new WebSocketFacade(this);
        preloginUI = new PreloginUI(this, serverFacade);
        postloginUI = new PostloginUI(this, serverFacade);
        gameplayUI = new GameplayUI(this);
        isRunning = true;
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

    public void updateGame(Game updatedGame) {
        // Add a field to store the current game state
        ChessGame game = updatedGame.getGame(); // Update the ChessGame state

        // Determine the current player based on the usernames in the Game object
        if (this.authToken != null) {
            String currentUser = this.authToken; // Replace this with your method of getting the current username
            if (currentUser.equals(updatedGame.getWhiteUsername())) {
                this.currentPlayer = "white";
            } else if (currentUser.equals(updatedGame.getBlackUsername())) {
                this.currentPlayer = "black";
            } else {
                this.currentPlayer = "observer";
            }
        }

        // Additional logic based on the game state
        // For example, determining if the current user can move
        if (this.currentPlayer.equals("white") || this.currentPlayer.equals("black")) {
            this.playing = true;
            this.canMove = true; // You might want to add more sophisticated logic to determine if the player can move
        } else {
            this.playing = false;
            this.canMove = false;
        }

        // Update the UI with the new game state
        this.gameplayUI.redraw(game, null, null); // Adapt parameters as needed
    }

    public void resignGame() throws Exception {
        if (!playing) {
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
        playing = false;
        canMove = false;
    }

    public void leaveGame() throws Exception {
        if (!playing) {
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
        playing = false;
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
    public void joinPlayer(String color, int gameID) {
        // Create a JSON object for joining as a player
        JsonObject joinJson = new JsonObject();
        joinJson.addProperty("type", "joinPlayer");
        joinJson.addProperty("gameID", gameID);
        joinJson.addProperty("color", color);
        joinJson.addProperty("authToken", this.authToken);

        // Send the join message to the server or WebSocket
        webSocketFacade.sendMessage(joinJson.toString());

        // Update the client state
        this.playing = true;
        this.canMove = true; // This might be true or false depending on the game logic
        this.curID = gameID;
        this.currentPlayer = color;
    }

    // Method to join a game as an observer
    public void joinObserver(int gameID) {
        // Create a JSON object for joining as an observer
        JsonObject joinJson = new JsonObject();
        joinJson.addProperty("type", "joinObserver");
        joinJson.addProperty("gameID", gameID);
        joinJson.addProperty("authToken", this.authToken);

        // Send the join message to the server or WebSocket
        webSocketFacade.sendMessage(joinJson.toString());

        // Update the client state
        this.playing = false;
        this.canMove = false;
        this.curID = gameID;
        this.currentPlayer = "observer";
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
