package ui;

import client.ChessClient;

public class PreloginUI {

    private final ChessClient client;

    public PreloginUI(ChessClient client) {
        this.client = client;
    }

    public void displayMenu() {
        System.out.println("Pre-login Menu:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Quit");
        // Add logic to handle user input
    }

    // Add methods to handle login, register, and quit
}
