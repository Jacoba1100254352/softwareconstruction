package clients;

import serverFacade.ServerFacade;
import testFactory.TestFactory;
import ui.*;

public class ChessClient {
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;

    private final WebSocketClient webSocketClient; // Add WebSocketClient as a member

    private String authToken;

    private String clientUsername; // Username of the current user
    private boolean isAdmin;
    private boolean isRunning;
    private boolean isLoggedIn;

    public ChessClient() {
        ServerFacade serverFacade = new ServerFacade("http://localhost:" + TestFactory.getServerPort());

        this.webSocketClient = new WebSocketClient(this);

        preloginUI = new PreloginUI(this, serverFacade);
        postloginUI = new PostloginUI(this, webSocketClient, serverFacade);
        gameplayUI = new GameplayUI(this, webSocketClient);

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
