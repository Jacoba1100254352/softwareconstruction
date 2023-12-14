package ui;

import chess.ChessMove;
import chess.ChessMoveImpl;
import chess.ChessPiece;
import chess.ChessPositionImpl;
import clients.ChessClient;
import clients.WebSocketClient;
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
    private static final Logger LOGGER = Logger.getLogger(PostloginUI.class.getName());
    private final ChessClient chessClient;
    private final WebSocketClient webSocketClient; // Added instance of WebSocketClient
    private final ServerFacade serverFacade;
    private final Map<Integer, Integer> gameMap = new HashMap<>();

    public PostloginUI(ChessClient chessClient, WebSocketClient webSocketClient, ServerFacade serverFacade) {
        this.chessClient = chessClient;
        this.webSocketClient = webSocketClient; // Initialize WebSocketClient
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
        // Formatting
        System.out.println();

        switch (choice) {
            case 0 -> displayHelp();
            case 1 -> createGame();
            case 2 -> listGames();
            case 3 -> joinGame();
            case 4 -> joinAsObserver();
            case 5 -> logout();
            case 6 -> resignGame();
            case 7 -> leaveGame();
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
            String response = serverFacade.sendPostRequest("/game", new Gson().toJson(jsonRequest), chessClient.getAuthToken());
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
            String response = serverFacade.sendGetRequest("/game", chessClient.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                JsonArray games = responseObject.get("games").getAsJsonArray();

                if (games.isEmpty()) {
                    System.out.println("No games to list.");
                    return;
                }

                int number = 1;
                for (JsonElement game : games) {
                    // Get game info
                    Integer gameId = game.getAsJsonObject().get("gameID").getAsInt();
                    String gameName = game.getAsJsonObject().get("gameName").getAsString();

                    // Get Players
                    String whitePlayer = game.getAsJsonObject().get("whiteUsername").isJsonNull() ? "None" : game.getAsJsonObject().get("whiteUsername").getAsString();
                    String blackPlayer = game.getAsJsonObject().get("blackUsername").isJsonNull() ? "None" : game.getAsJsonObject().get("blackUsername").getAsString();

                    System.out.println(number + ". " + gameName + "\tWHITE: " + whitePlayer + " BLACK: " + blackPlayer);
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

        try {
            if (asObserver) {
                webSocketClient.joinObserver(gameId);
                System.out.println("Joined game as observer successfully.");
            } else {
                System.out.print("Enter color (WHITE/BLACK): ");
                String colorStr = scanner.next().toUpperCase();
                webSocketClient.joinPlayer(colorStr, gameId);
                System.out.println("Joined game successfully.");

                // Draw the board
                chessClient.getGameplayUI().drawChessboard();

                // Prompt user for a move if they're joining as a player
                promptForMove();
            }
        } catch (Exception e) {
            LOGGER.severe("Join game error: " + e.getMessage());
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void promptForMove() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your move (e.g., e2 e4, or e7 e8 Q for pawn promotion): ");
        String moveInput = scanner.nextLine();
        String[] moveParts = moveInput.split(" ");

        // Check for standard move format or move with promotion
        if (moveParts.length != 2 && moveParts.length != 3) {
            System.out.println("Invalid move format.");
            return;
        }

        try {
            // Use the third part of the input as the promotion piece if provided
            String promotionPiece = (moveParts.length == 3) ? moveParts[2] : null;
            ChessMove move = parseMoveInput(moveParts[0], moveParts[1], promotionPiece);
            webSocketClient.makeMove(move);
            System.out.println("Move made successfully.");
        } catch (Exception e) {
            LOGGER.severe("Make move error: " + e.getMessage());
            System.out.println("Failed to make move: " + e.getMessage());
        }
    }

    // You need to implement this method to convert user input into a ChessMove object
    private ChessMove parseMoveInput(String from, String to, String promotion) throws IllegalArgumentException {
        // Parse the 'from' and 'to' strings into ChessPositionImpl objects
        ChessPositionImpl startPos = parsePosition(from);
        ChessPositionImpl endPos = parsePosition(to);

        // Parse the promotion piece, if provided
        ChessPiece.PieceType promotionPiece = parsePromotionPiece(promotion);

        // Return a new ChessMove object
        return new ChessMoveImpl(startPos, endPos, promotionPiece);
    }

    private ChessPositionImpl parsePosition(String position) throws IllegalArgumentException {
        if (position.length() != 2)
            throw new IllegalArgumentException("Invalid position format.");

        char colChar = position.toLowerCase().charAt(0);
        int row = Integer.parseInt(position.substring(1));
        int col = colChar - 'a' + 1; // Assuming 'a' is 1, 'b' is 2, etc.

        return new ChessPositionImpl(row, col);
    }

    private ChessPiece.PieceType parsePromotionPiece(String promotion) {
        if (promotion == null || promotion.isEmpty()) {
            return null; // No promotion piece specified
        }

        try {
            return ChessPiece.PieceType.valueOf(promotion.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid promotion piece type.");
        }
    }

    private void logout() {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("authToken", chessClient.getAuthToken());

        try {
            String response = serverFacade.sendDeleteRequest("/session", new Gson().toJson(jsonRequest), chessClient.getAuthToken());
            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                chessClient.setAuthToken(null);
                chessClient.transitionToPreloginUI();
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

    private void resignGame() {
        try {
            webSocketClient.resignGame();
            System.out.println("You have resigned from the game.");
        } catch (Exception e) {
            LOGGER.severe("Error resigning from game: " + e.getMessage());
            System.out.println("Failed to resign from game: " + e.getMessage());
        }
    }

    private void leaveGame() {
        try {
            webSocketClient.leaveGame();
            System.out.println("You have left the game.");
        } catch (Exception e) {
            LOGGER.severe("Error leaving game: " + e.getMessage());
            System.out.println("Failed to leave game: " + e.getMessage());
        }
    }
}
