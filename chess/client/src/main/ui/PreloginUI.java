package ui;

import clients.ChessClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import requests.LoginRequest;
import requests.RegisterRequest;
import serverFacade.ServerFacade;
import serverFacade.ServerFacadeException;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreloginUI {
    private static final Logger LOGGER = Logger.getLogger(PreloginUI.class.getName());
    private final ChessClient chessClient;
    private final ServerFacade serverFacade;

    public PreloginUI(ChessClient chessClient, ServerFacade serverFacade) {
        this.chessClient = chessClient;
        this.serverFacade = serverFacade;
    }

    static {
        LOGGER.setLevel(Level.WARNING);
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
                LOGGER.warning("Invalid input. Please enter a valid number.");
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

        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            String response = serverFacade.sendPostRequest("/session", (new Gson()).toJson(loginRequest), null);
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                if (chessClient.isDebugMode()) {
                System.out.print("Login response object" + responseObject);
                }

                String authToken = responseObject.get("authToken").getAsString();

                // Formatting
                System.out.println();

                chessClient.setAuthToken(authToken);
                chessClient.transitionToPostloginUI();

                LOGGER.info("Logged in successfully.\n");
            } else {
                LOGGER.warning("Login failed: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException e) {
            if (e.getMessage().contains("HTTP response code: 401")) {
                LOGGER.warning("Invalid username or password.");
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

        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            String response = serverFacade.sendPostRequest("/user", (new Gson()).toJson(registerRequest), null);
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                String authToken = responseObject.get("authToken").getAsString();
                chessClient.setAuthToken(authToken);
                chessClient.transitionToPostloginUI();
                LOGGER.info("Registered and logged in successfully.");
            } else {
                LOGGER.warning("Registration failed: " + responseObject.get("message").getAsString());
            }
        } catch (ServerFacadeException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                LOGGER.warning("Username already taken. Please choose a different username or log in.");
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
            case 3 -> chessClient.exit();
            default -> LOGGER.warning("Invalid choice. Please enter a number between 1 and 3.");
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
