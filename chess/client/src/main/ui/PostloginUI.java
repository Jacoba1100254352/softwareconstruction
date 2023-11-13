package ui;

import client.ChessClient;

public class PostloginUI {

    private final ChessClient client;

    public PostloginUI(ChessClient client) {
        this.client = client;
    }

    public void displayMenu() {
        System.out.println("Post-login Menu:");
        System.out.println("1. Create Game");
        System.out.println("2. List Games");
        System.out.println("3. Join Game");
        System.out.println("4. Logout");
        // Add logic to handle user input
    }

    // Add methods to handle game creation, listing, joining, and logout
}
