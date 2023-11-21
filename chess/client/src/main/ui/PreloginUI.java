package ui;

import client.ChessClient;
import server.ServerFacade;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.ServerFacadeException;

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
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Quit");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                client.exit();
                break;
            default:
                System.out.println("Invalid choice.");
        }
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
            String response = serverFacade.sendPostRequest("/session", jsonRequest.toString());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                String authToken = responseObject.get("authToken").getAsString();
                client.setAuthToken(authToken);
                client.transitionToPostloginUI();
                System.out.println("Logged in successfully.");
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
            String response = serverFacade.sendPostRequest("/user", jsonRequest.toString());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();

            if (responseObject.has("authToken") && !responseObject.get("authToken").isJsonNull()) {
                String authToken = responseObject.get("authToken").getAsString();
                client.setAuthToken(authToken);
                client.transitionToPostloginUI();
                System.out.println("Registered and logged in successfully.");
            } else {
                String errorMessage = responseObject.has("message") && !responseObject.get("message").isJsonNull() ? responseObject.get("message").getAsString() : "Unknown error occurred";
                System.out.println("Registration failed: " + errorMessage);
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
}