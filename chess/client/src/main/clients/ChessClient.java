package clients;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import serverFacade.ServerFacade;
import ui.*;
import webSocketMessages.serverMessages.*;

import java.net.URI;

public class ChessClient {
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private String authToken;
    private boolean isAdmin;
    private boolean isRunning;
    private boolean isLoggedIn;

    public ChessClient() {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

        preloginUI = new PreloginUI(this, serverFacade);
        postloginUI = new PostloginUI(this, serverFacade);
        gameplayUI = new GameplayUI();
        isRunning = true;

        connectToGameServer();
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

    public void connectToGameServer() {
        try {
            URI serverUri = new URI("ws://localhost:8081/ws");
            WebSocketClient webSocketClient = new WebSocketClient(serverUri);
            webSocketClient.addMessageHandler(this::handleWebSocketMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleWebSocketMessage(String message) {
        Gson gson = new Gson();
        JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
        String messageType = jsonMessage.get("serverMessageType").getAsString();

        switch (ServerMessage.ServerMessageType.valueOf(messageType)) {
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = gson.fromJson(jsonMessage, LoadGameMessage.class);
                // Assuming loadGameMessage contains necessary information about the game state
                // FIXME: Update the game board with this new state
                //gameplayUI.updateGameState(loadGameMessage.getLoadGameMessage());
                break;

            case ERROR:
                ErrorMessage errorMessage = gson.fromJson(jsonMessage, ErrorMessage.class);
                // Display the error message to the user
                System.out.println("Error received: " + errorMessage.getErrorMessage());
                // FIXME: You might also want to update the UI to reflect that an error occurred
                //gameplayUI.displayError(errorMessage.getErrorMessage());
                break;
            case NOTIFICATION:
                NotificationMessage notificationMessage = gson.fromJson(jsonMessage, NotificationMessage.class);
                // Display the notification message
                System.out.println("Notification: " + notificationMessage.getNotificationMessage());
                // FIXME: Update the UI to show the notification, if necessary
                //gameplayUI.showNotification(notificationMessage.getNotificationMessage());
                break;

        }
    }

    public void exit() {
        isRunning = false;
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
