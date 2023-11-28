package client;

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