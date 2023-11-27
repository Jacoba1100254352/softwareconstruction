package ui;

import client.ChessClient;
import serverFacade.ServerFacade;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import serverFacade.ServerFacadeException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Logger;

public class PostloginUI {
    private final ChessClient client;
    private final ServerFacade serverFacade;
    private static final Logger LOGGER = Logger.getLogger(PostloginUI.class.getName());

    public PostloginUI(ChessClient client, ServerFacade serverFacade) {
        this.client = client;
        this.serverFacade = serverFacade;
    }

    public void displayMenu() {
        System.out.println("Post-login Menu:");
        System.out.println("1. Create Game");
        System.out.println("2. List Games");
        System.out.println("3. Join Game");
        System.out.println("4. Logout");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1: createGame(); break;
            case 2: listGames(); break;
            case 3: joinGame(); break;
            case 4: logout(); break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void createGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("gameName", gameName);

        try {
            String response = serverFacade.sendPostRequest("/game", new Gson().toJson(jsonRequest), client.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                System.out.println("Game created successfully. Game ID: " + responseObject.get("gameID").getAsInt());
            } else {
                System.out.println("Failed to create game: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException | IOException | URISyntaxException e) {
            LOGGER.severe("Create game error: " + e.getMessage());
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            String response = serverFacade.sendGetRequest("/game", client.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                System.out.println("Games: " + responseObject.get("games").getAsJsonArray().toString());
            } else {
                System.out.println("Failed to list games: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException | IOException | URISyntaxException e) {
            LOGGER.severe("List games error: " + e.getMessage());
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void joinGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter game ID to join: ");
        int gameId = scanner.nextInt();
        System.out.print("Enter color (WHITE/BLACK): ");
        String color = scanner.next().toUpperCase();

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("playerColor", color);
        jsonRequest.addProperty("gameID", gameId);

        try {
            String response = serverFacade.sendPutRequest("/game", new Gson().toJson(jsonRequest), client.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                System.out.println("Joined game successfully.");
            } else {
                System.out.println("Failed to join game: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException | IOException | URISyntaxException e) {
            LOGGER.severe("Join game error: " + e.getMessage());
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void logout() {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("authToken", client.getAuthToken());

        try {
            String response = serverFacade.sendDeleteRequest("/session", new Gson().toJson(jsonRequest), client.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                client.setAuthToken(null);
                client.transitionToPreloginUI();
                System.out.println("Logged out successfully.");
            } else {
                System.out.println("Failed to logout: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException | IOException | URISyntaxException e) {
            LOGGER.severe("Logout error: " + e.getMessage());
            System.out.println("Failed to logout: " + e.getMessage());
        }
    }
}
