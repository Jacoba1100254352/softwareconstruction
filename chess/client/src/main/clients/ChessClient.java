package clients;

import serverFacade.ServerFacade;
import testFactory.TestFactory;
import ui.GameplayUI;
import ui.PostloginUI;
import ui.PreloginUI;

public class ChessClient {
    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;

    private String authToken;

    private String clientUsername; // Username of the current user
    private boolean isAdmin;
    private boolean isRunning;
    private boolean isLoggedIn;

    public ChessClient() {
        ServerFacade serverFacade = new ServerFacade("http://localhost:" + TestFactory.getServerPort());

        // Add WebSocketClient as a member
        WebSocketClient webSocketClient = new WebSocketClient(this);

        preloginUI = new PreloginUI(this, serverFacade);
        postloginUI = new PostloginUI(this, webSocketClient, serverFacade);
        gameplayUI = new GameplayUI(this, webSocketClient);

        isRunning = true;
    }

    public static void main(String[] args) {
        (new ChessClient()).run();
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

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
