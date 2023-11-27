package ui;

import client.ChessClient;
import com.google.gson.*;
import serverFacade.ServerFacade;
import serverFacade.ServerFacadeException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class PostloginUI {
    private final ChessClient client;
    private final ServerFacade serverFacade;
    private static final Logger LOGGER = Logger.getLogger(PostloginUI.class.getName());
    private final Map<Integer, Integer> gameMap = new HashMap<>();

    public PostloginUI(ChessClient client, ServerFacade serverFacade) {
        this.client = client;
        this.serverFacade = serverFacade;
    }

    public void displayMenu() {
        System.out.println("Post-login Menu:");
        System.out.println("0. Help");
        System.out.println("1. Create Game");
        System.out.println("2. List Games");
        System.out.println("3. Join Game");
        System.out.println("4. Join as Observer");
        System.out.println("5. Logout");

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        // Validate user input
        while (choice < 0 || choice > 5) {
            System.out.print("Enter choice: ");
            try {
                choice = scanner.nextInt();
                processUserChoice(choice);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }

        // Formatting
        System.out.println();
    }

    private void processUserChoice(int choice) {
        switch (choice) {
            case 0 -> displayHelp();
            case 1 -> createGame();
            case 2 -> listGames();
            case 3 -> joinGame();
            case 4 -> joinAsObserver();
            case 5 -> logout();
            default -> printInvalidChoice();
        }
    }

    private void printInvalidChoice() {
        System.out.println("Invalid choice.");
    }

    private void displayHelp() {
        System.out.println("Help Menu:");
        System.out.println("0. Help - Displays this help menu.");
        System.out.println("1. Create Game - Create a new game on the server.");
        System.out.println("2. List Games - Show a list of available games.");
        System.out.println("3. Join Game - Join an existing game as a player.");
        System.out.println("4. Join as Observer - Observe an existing game.");
        System.out.println("5. Logout - Log out of the application.");
        System.out.println();
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
        gameMap.clear(); // Clear the previous mapping
        try {
            String response = serverFacade.sendGetRequest("/game", client.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                JsonArray games = responseObject.get("games").getAsJsonArray();

                if (games.isEmpty()) {
                    System.out.println("No games to list.");
                    return;
                }

                int number = 1;
                for (JsonElement game : games) {
                    Integer gameId = game.getAsJsonObject().get("gameID").getAsInt();
                    String gameName = game.getAsJsonObject().get("gameName").getAsString();
                    System.out.println(number + ". " + gameName);
                    gameMap.put(number++, gameId); // Map list number to game ID
                }
            } else {
                System.out.println("Failed to list games: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException | IOException | URISyntaxException e) {
            LOGGER.severe("List games error: " + e.getMessage());
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void joinGame() {
        joinGame(false);
    }

    private void joinAsObserver() {
        joinGame(true);
    }

    private void joinGame(boolean asObserver) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter game number to join: ");
        int gameNumber = scanner.nextInt();
        Integer gameId = gameMap.get(gameNumber);

        if (gameId == null) {
            System.out.println("Invalid game number.");
            return;
        }

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("gameID", gameId);

        if (!asObserver) {
            System.out.print("Enter color (WHITE/BLACK): ");
            String color = scanner.next().toUpperCase();
            jsonRequest.addProperty("playerColor", color);
        }

        try {
            String response = serverFacade.sendPutRequest("/game", new Gson().toJson(jsonRequest), client.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                System.out.println();
                System.out.println(asObserver ? "Joined game as observer successfully." : "Joined game successfully.");
                System.out.println();

                // Display Game Board
                client.drawChessboard();
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
                LOGGER.severe("Logout error: " + responseObject.get("message").getAsString());
                System.out.println("Failed to logout: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException | IOException | URISyntaxException e) {
            LOGGER.severe("Logout error: " + e.getMessage());
            System.out.println("Failed to logout: " + e.getMessage());
        }
    }
}
