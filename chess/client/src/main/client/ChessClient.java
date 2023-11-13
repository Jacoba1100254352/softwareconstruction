package client;

import ui.*;

public class ChessClient {

    private final PreloginUI preloginUI;
    private final PostloginUI postloginUI;
    private final GameplayUI gameplayUI;
    private boolean isRunning;

    public ChessClient() {
        preloginUI = new PreloginUI(this);
        postloginUI = new PostloginUI(this);
        gameplayUI = new GameplayUI(this);
        isRunning = true;
    }

    public static void main(String[] args) {
        ChessClient client = new ChessClient();
        client.run();
    }

    private void run() {
        while (isRunning) {
            preloginUI.displayMenu();
            // Add logic to switch between UIs based on user state
        }
    }

    public void exit() {
        isRunning = false;
    }

    // Add methods to transition between different UIs
}
