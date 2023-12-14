package clients;

import WebSocketFacade.WebSocketFacade;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

public class WebSocketClient {
    private final WebSocketFacade webSocketFacade;

    private final String authToken;
    private Integer gameID;

    private boolean isPlayer;
    private boolean canMove;

    public WebSocketClient(ChessClient chessClient) {
        this.authToken = chessClient.getAuthToken();
        this.gameID = null;

        this.isPlayer = false;
        this.canMove = false;

        // Initialize WebSocketFacade with this instance
        this.webSocketFacade = new WebSocketFacade(chessClient, this);
    }

    public void resignGame() throws Exception {
        if (!isPlayer) {
            throw new Exception("Must be in a game to resign");
        }

        try {
            ResignCommand command = new ResignCommand(this.authToken, this.gameID);
            String message = new Gson().toJson(command);

            // Send the message to the server or WebSocket
            webSocketFacade.sendMessage(message);

            // Update client state
            isPlayer = false;
            canMove = false;
        } catch (Exception e) {
            System.err.println("Error in resignGame: " + e.getMessage());
        }
    }

    public void leaveGame() throws Exception {
        if (!isPlayer) {
            throw new Exception("Must be in a game to leave it");
        }

        try {
            LeaveCommand command = new LeaveCommand(this.authToken, this.gameID);
            String message = new Gson().toJson(command);

            // Send the move message to the server or WebSocket
            webSocketFacade.sendMessage(message);

            // Update the client's state
            isPlayer = false;
            canMove = false;
        } catch (Exception e) {
            System.err.println("Error in leaveGame: " + e.getMessage());
        }
    }

    // Method to make a move in the game
    public void makeMove(ChessMove move) throws Exception {
        if (!canMove) {
            throw new Exception("It's not your turn to move.");
        }

        try {
            MakeMoveCommand command = new MakeMoveCommand(this.authToken, this.gameID, move);
            String message = new Gson().toJson(command);

            // Send the move message to the server or WebSocket
            webSocketFacade.sendMessage(message);

            // Update the client's state
            this.canMove = false;
        } catch (Exception e) {
            System.err.println("Error in makeMove: " + e.getMessage());
        }
    }

    // Method to join a game as a player
    public void joinPlayer(String colorStr, Integer gameID) {

        ChessGame.TeamColor teamColor;
        if (colorStr.equals("WHITE")) {
            teamColor = ChessGame.TeamColor.WHITE;
            this.canMove = true;
        } else {
            teamColor = ChessGame.TeamColor.BLACK;
            this.canMove = false;
        }

        try {
            JoinPlayerCommand command = new JoinPlayerCommand(this.authToken, gameID, teamColor);
            String message = new Gson().toJson(command);

            // Send the join message to the server or WebSocket
            webSocketFacade.sendMessage(message);

            // Update the client state
            this.isPlayer = true;
            this.gameID = gameID;
        } catch (Exception e) {
            System.err.println("Error in joinPlayer: " + e.getMessage());
        }
    }

    // Method to join a game as an observer
    public void joinObserver(Integer gameID) {
        try {
            JoinObserverCommand command = new JoinObserverCommand(this.authToken, gameID);
            String message = new Gson().toJson(command);

            // Send the join message to the server or WebSocket
            webSocketFacade.sendMessage(message);

            // Update the client state
            this.isPlayer = false;
            this.canMove = false;
            this.gameID = gameID;
        } catch (Exception e) {
            System.err.println("Error in joinObserver: " + e.getMessage());
        }
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
            System.err.println("\nError: " + error.getErrorMessage());
        } else {
            // Default handling for other types of messages
            System.out.println("\n" + message);
        }
    }
}
