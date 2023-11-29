package clients;

import serverFacade.ServerFacade;
import ui.*;

public class ChessClient {
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private String authToken;
    private boolean isAdmin;
    private boolean isRunning;
    private boolean isLoggedIn;
    private WebSocketClient webSocketClient;

    public ChessClient() {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        preloginUI = new PreloginUI(this, serverFacade);
        postloginUI = new PostloginUI(this, serverFacade);
        gameplayUI = new GameplayUI();
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

    public void connectToGameServer() {
        WebSocketClient webSocketClient = new WebSocketClient(new WebSocketClient.MessageHandler() {
            public void handleMessage(String message) {
                // Implement handling of incoming WebSocket messages
                // For example, update the UI based on the message content
                System.out.println("Message received from server: " + message);
            }
        });
        webSocketClient.connectToWebSocket("ws://localhost:8081/ws");
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
