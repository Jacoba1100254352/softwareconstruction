package ui;

import clients.ChessClient;
import serverFacade.ServerFacade;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import serverFacade.ServerFacadeException;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

public class PreloginUI {
    private static final Logger LOGGER = Logger.getLogger(PreloginUI.class.getName());
    private final ChessClient client;
    private final ServerFacade serverFacade;

    public PreloginUI(ChessClient client, ServerFacade serverFacade) {
        this.client = client;
        this.serverFacade = serverFacade;
    }

    public void displayMenu() {
        System.out.println("Pre-login Menu:");
        System.out.println("0. Help");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Quit");

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        // Validate user input
        while (choice < 0 || choice > 3) {
            System.out.print("Enter choice (0-3): ");
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

    private void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("username", username);
        jsonRequest.addProperty("password", password);

        try {
            String response = serverFacade.sendPostRequest("/session", jsonRequest.toString(), null);
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                String authToken = responseObject.get("authToken").getAsString();
                boolean isAdmin = responseObject.has("isAdmin") && responseObject.get("isAdmin").getAsBoolean();

                System.out.println();

                client.setAuthToken(authToken);
                client.setAdmin(isAdmin);
                client.transitionToPostloginUI();

                System.out.println("Logged in successfully.\n");
            } else {
                System.out.println("Login failed: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException e) {
            if (e.getMessage().contains("HTTP response code: 401")) {
                System.out.println("Invalid username or password.");
            } else {
                LOGGER.severe("Login error: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.severe("Unexpected error: " + e.getMessage());
        }
    }

    private void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("username", username);
        jsonRequest.addProperty("password", password);
        jsonRequest.addProperty("email", email);

        try {
            String response = serverFacade.sendPostRequest("/user", jsonRequest.toString(), null);
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                String authToken = responseObject.get("authToken").getAsString();
                client.setAuthToken(authToken);
                client.transitionToPostloginUI();
                System.out.println("Registered and logged in successfully.");
            } else {
                System.out.println("Registration failed: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                System.out.println("Username already taken. Please choose a different username or log in.");
            } else {
                LOGGER.severe("Registration error: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.severe("Unexpected error: " + e.getMessage());
        }
    }

    private void processUserChoice(int choice) {
        // Formatting
        System.out.println();

        switch (choice) {
            case 0 -> displayHelp();
            case 1 -> login();
            case 2 -> register();
            case 3 -> client.exit();
            default -> System.out.println("Invalid choice. Please enter a number between 1 and 3.");
        }
    }

    private void displayHelp() {
        System.out.println();
        System.out.println("Help Menu:");
        System.out.println("0. Help - Displays this help menu.");
        System.out.println("1. Login - Log in to the application.");
        System.out.println("2. Register - Register a new user.");
        System.out.println("3. Quit - Exits the program.");
        System.out.println();
    }
}
