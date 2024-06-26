package ui;


import GameStateUpdateListener.GameStateUpdateListener;
import chess.gameplay.*;
import chess.pieces.ChessPiece;
import clients.ChessClient;
import clients.WebSocketClient;
import com.google.gson.*;
import requests.JoinGameRequest;
import serverFacade.ServerFacade;
import serverFacade.ServerFacadeException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PostloginUI implements GameStateUpdateListener
{
	private static final Logger LOGGER = Logger.getLogger(PostloginUI.class.getName());

	static {
		LOGGER.setLevel(Level.WARNING);
	}

	private final ChessClient chessClient;
	private final WebSocketClient webSocketClient;
	private final ServerFacade serverFacade;
	private final Map<Integer, Integer> gameMap = new HashMap<>();
	private ChessGame currentGame;
	private boolean isInGame = false;
	
	public PostloginUI(ChessClient chessClient, ServerFacade serverFacade) {
		this.chessClient = chessClient;
		this.webSocketClient = new WebSocketClient(chessClient);
		this.serverFacade = serverFacade;
		this.webSocketClient.getWebSocketFacade().setGameStateUpdateListener(this);
	}
	
	@Override
	public void onGameStateUpdate(ChessGame updatedGame) {
		this.currentGame = updatedGame;
		String playerColor = webSocketClient.getPlayerColor();
		chessClient.getGameplayUI().redrawGame(this.currentGame, playerColor);
	}
	
	public void displayMenu() {
		if (isInGame) {
			return;
		}
		
		System.out.println("Post-login Menu:");
		System.out.println("0. Help");
		System.out.println("1. Create Game");
		System.out.println("2. List Games");
		System.out.println("3. Join Game");
		System.out.println("4. Join as Observer");
		System.out.println("5. Logout");
		
		Scanner scanner = new Scanner(System.in);
		int choice = getInput(scanner, 0, 5);
		processUserChoice(choice);
	}
	
	private int getInput(Scanner scanner, int min, int max) {
		int choice = -1;
		while (choice < min || choice > max) {
			System.out.print("Enter choice: ");
			if (scanner.hasNextInt()) {
				choice = scanner.nextInt();
			} else {
				LOGGER.warning("Invalid input. Please enter a valid number.");
				scanner.next(); // Clear the invalid input
			}
		}
		scanner.nextLine(); // Consume the newline left after nextInt()
		return choice;
	}
	
	private void processUserChoice(int choice) {
		switch (choice) {
			case 0 -> displayHelp();
			case 1 -> createGame();
			case 2 -> listGames();
			case 3 -> joinGame();
			case 4 -> joinAsObserver();
			case 5 -> logout();
			case 6 -> resignGame();
			case 7 -> leaveGame();
			default -> LOGGER.warning("Invalid choice selected: " + choice);
		}
	}
	
	private void displayHelp() {
		if (chessClient.isDebugMode()) {
			LOGGER.info("Displaying Help Menu");
		}
		
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
				LOGGER.warning("Failed to create game: " + responseObject.get("message").getAsString());
			}
		} catch (ServerFacadeException | IOException | URISyntaxException e) {
			LOGGER.severe("Create game error: " + e.getMessage());
		}
	}
	
	private void listGames() {
		gameMap.clear(); // Clear the previous game list mapping
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
				LOGGER.warning("Failed to list games: " + responseObject.get("message").getAsString());
			}
		} catch (ServerFacadeException | IOException | URISyntaxException e) {
			LOGGER.severe("List games error: " + e.getMessage());
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
		if (gameMap.isEmpty()) {
			LOGGER.warning("Please list games first.");
			return;
		}
		
		System.out.print("Enter game number to join: ");
		int gameNumber = scanner.nextInt();
		Integer gameID = gameMap.get(gameNumber);
		
		if (gameID == null) {
			LOGGER.warning("Invalid game number.");
			return;
		}
		
		try {
			if (asObserver) {
				webSocketClient.joinObserver(gameID);
				LOGGER.info("Joined game as observer successfully.");
			} else {
				System.out.print("Enter color (WHITE/BLACK): ");
				String colorStr = scanner.next().toUpperCase();
				
				boolean joinSuccess = joinGameHttp(gameID, colorStr);
				if (!joinSuccess) {
					LOGGER.warning("Failed to join the game.");
					return;
				}
				
				webSocketClient.joinPlayer(colorStr, gameID);
				LOGGER.info("Joined game successfully.");
				
				// Initialize currentGame and reset its board
				this.currentGame = new ChessGameImpl();
				this.currentGame.getBoard().resetBoard();
				
				// Set isInGame to true
				isInGame = true;
				
				// Display the board
				chessClient.getGameplayUI().displayBoard(currentGame.getBoard(), colorStr.toLowerCase(), null, null);
				
				// Prompt user for a move if they're joining as a player
				promptForMove();
			}
		} catch (Exception e) {
			LOGGER.severe("Join game error: " + e.getMessage());
		}
	}
	
	private boolean joinGameHttp(Integer gameID, String colorStr) {
		try {
			JoinGameRequest joinRequest = new JoinGameRequest(chessClient.getAuthToken(), gameID, colorStr);
			String response = serverFacade.sendPutRequest("/game", new Gson().toJson(joinRequest), chessClient.getAuthToken());
			JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
			return responseObject.get("success").getAsBoolean();
		} catch (ServerFacadeException | IOException | URISyntaxException e) {
			LOGGER.severe("Error joining game: " + e.getMessage());
			return false;
		}
	}
	
	private void promptForMove() {
		String playerColor = webSocketClient.getPlayerColor();
		chessClient.getGameplayUI().redrawGame(this.currentGame, playerColor);
		
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("Options:");
			System.out.println("1. Make Move");
			System.out.println("2. Resign Game");
			System.out.println("3. Leave Game");
			
			int choice = getInput(scanner, 1, 3);
			
			switch (choice) {
				case 1:
					makeMove(scanner);
					return; // Return after move is made
				case 2:
					resignGame();
					return; // Return after resigning
				case 3:
					leaveGame();
					return; // Return after leaving
				default:
					LOGGER.warning("Invalid choice. Please select a valid option.");
			}
		}
	}
	
	private void makeMove(Scanner scanner) {
		if (webSocketClient.getPlayerTeamColor() == null || currentGame.getTeamTurn() != webSocketClient.getPlayerTeamColor()) {
			LOGGER.warning("It's not your turn.");
			return;
		}
		
		System.out.println("Enter your move (e.g., e2 e4, or e7 e8 Q for pawn promotion): ");
		String moveInput = scanner.nextLine();
		String[] moveParts = moveInput.split(" ");
		
		// Check for standard move format or move with promotion
		if (moveParts.length != 2 && moveParts.length != 3) {
			LOGGER.warning("Invalid move format. Please enter a valid move.");
			return;
		}
		
		try {
			// Use the third part of the input as the promotion piece if provided
			String promotionPiece = (moveParts.length == 3) ? moveParts[2] : null;
			ChessMove move = parseMoveInput(moveParts[0], moveParts[1], promotionPiece);
			webSocketClient.makeMove(move);
			
			LOGGER.info("Move made successfully.");
		} catch (Exception e) {
			LOGGER.severe("Make move error: " + e.getMessage());
		}
	}
	
	
	// Convert user input into a ChessMove object
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
		if (position.length() != 2) {
			throw new IllegalArgumentException("Invalid position format.");
		}
		
		char colChar = position.toLowerCase().charAt(0);
		int row = Integer.parseInt(position.substring(1));
		int col = colChar - 'a' + 1;
		
		return new ChessPositionImpl(row, col);
	}
	
	private ChessPiece.PieceType parsePromotionPiece(String promotion) {
		if (promotion == null || promotion.isEmpty()) {
			return null;
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
				
				if (chessClient.isDebugMode()) {
					LOGGER.info("Logged out successfully.");
				}
			} else {
				LOGGER.warning("Logout error: " + responseObject.get("message").getAsString());
			}
		} catch (ServerFacadeException | IOException | URISyntaxException e) {
			LOGGER.severe("Logout error: " + e.getMessage());
		}
	}
	
	private void resignGame() {
		try {
			webSocketClient.resignGame();
			LOGGER.info("Resigned from game successfully.");
			isInGame = false; // Reset isInGame
		} catch (Exception e) {
			LOGGER.severe("Error resigning from game: " + e.getMessage());
		}
	}
	
	private void leaveGame() {
		try {
			webSocketClient.leaveGame();
			LOGGER.info("Left game successfully.");
			isInGame = false; // Reset isInGame
		} catch (Exception e) {
			LOGGER.severe("Error leaving game: " + e.getMessage());
		}
	}
}
