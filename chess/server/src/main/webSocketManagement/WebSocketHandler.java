package webSocketManagement;


import adapter.ChessMoveAdapter;
import adapter.ChessPosAdapter;
import chess.gameplay.ChessGame;
import chess.gameplay.ChessMove;
import chess.gameplay.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import models.AuthToken;
import models.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;


@WebSocket
public class WebSocketHandler
{
	private static final boolean DEBUG_MODE = false;
	private final ConnectionManager connectionManager;
	private final ObserverManager observerManager;
	private final MessageDispatcher messageDispatcher;
	private final GameDAO gameDAO;
	private final Gson gson;
	
	public WebSocketHandler() {
		connectionManager = new ConnectionManager();
		observerManager = new ObserverManager();
		messageDispatcher = new MessageDispatcher(connectionManager, DEBUG_MODE);
		
		gameDAO = new GameDAO();
		
		//gson = new GsonBuilder().registerTypeAdapter(ChessPosition.class, new ChessPosAdapter()).registerTypeAdapter(ChessMove.class, new ChessMoveAdapter()).create();
		gson = new Gson();// new GsonBuilder().registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter()).create();
	}
	
	// Handle incoming WebSocket messages
	@OnWebSocketMessage
	public void onMessage(Session session, String message) throws Exception {
		// Prepare space in the output between messages
		if (DEBUG_MODE) {
			System.out.println("\n");
		}
		
		// Deserialize the incoming message to a UserGameCommand
		UserGameCommand gameCmd = gson.fromJson(message, UserGameCommand.class);
		
		// Handle different command types
		switch (gameCmd.getCommandType()) {
			case JOIN_OBSERVER -> handleJoinObserver(session, gson.fromJson(message, JoinObserverCommand.class));
			case JOIN_PLAYER -> handleJoinPlayer(session, gson.fromJson(message, JoinPlayerCommand.class));
			case MAKE_MOVE -> {
				GsonBuilder gsonBuilder = new GsonBuilder()
						.registerTypeAdapter(ChessPosition.class, new ChessPosAdapter())
						.registerTypeAdapter(ChessMove.class, new ChessMoveAdapter());
				handleMove(session, (gsonBuilder.create()).fromJson(message, MakeMoveCommand.class));
			}
			case RESIGN -> handleResign(session, gson.fromJson(message, ResignCommand.class));
			case LEAVE -> handleLeave(session, gson.fromJson(message, LeaveCommand.class));
			default -> System.err.println("Error: Invalid command type in onMessage");
		}
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		if (DEBUG_MODE) {
			System.out.println("Connected: " + session);
		}
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		if (DEBUG_MODE) {
			System.out.println("Session closed with status " + statusCode + ". Reason: " + reason + ". For session " + session);
		}
	}
	
	@OnWebSocketError
	public void onError(Session session, Throwable throwable) {
		if (DEBUG_MODE) {
			System.err.println("WebSocketHandler Error: " + throwable.getMessage());
		}
	}
	
	private void handleJoinObserver(Session session, JoinObserverCommand gameCmd) throws Exception {
		Integer gameID = gameCmd.getGameID();
		Game game = gameDAO.findGameByID(gameID);
		
		// Find the game by ID and handle cases where the game doesn't exist
		if (game == null || game.getGameName() == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error 403: Bad request, game id not in database"); // "Error 404: Game not found"
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Find the username from the auth token
		String userName = findUser(session, gameCmd.getAuthString());
		if (userName == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error 401: Unauthorized");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Check if the user is already an observer
		if (observerManager.isObserver(userName, gameID)) {
			// Add the user as an observer if they are not already observing
			observerManager.add(gameID, new ObserverInstance(session, userName));
			connectionManager.add(gameID, new ConnectionInstance(session, userName));
			
			// Send game state to the new observer first
			if (game.getChessGame() != null) {
				LoadGameMessage loadGameMessage = new LoadGameMessage(game.getChessGame());
				messageDispatcher.sendMessage(session, loadGameMessage);
			} else {
				ErrorMessage errorMessage = new ErrorMessage("Error: Unable to load game data");
				messageDispatcher.sendMessage(session, errorMessage);
			}
			
			// Prepare and broadcast notification to all players and observers in the game, except the new observer
			NotificationMessage notification = new NotificationMessage(userName + " now observing");
			messageDispatcher.broadcastToGameExceptSession(gameCmd.getGameID(), notification, session);
		} else {
			// Handle the case where the user is already an observer
			ErrorMessage errorMessage = new ErrorMessage(userName + " is already observing game " + gameID);
			messageDispatcher.sendMessage(session, errorMessage);
		}
	}
	
	/**
	 * Handles a player's request to join a game.
	 *
	 * @param session The WebSocket session for the client.
	 * @param gameCmd The command containing details about the join request.
	 *
	 * @throws Exception If there's an error in processing the request.
	 */
	private void handleJoinPlayer(Session session, JoinPlayerCommand gameCmd) throws Exception {
		// Retrieve the player color and game ID from the command
		ChessGame.TeamColor color = gameCmd.getPlayerColor();
		Integer gameID = gameCmd.getGameID();
		
		// Access the game from the database
		GameDAO gameDAO = new GameDAO();
		Game game = gameDAO.findGameByID(gameID);
		
		// Check if the game is valid
		if (game == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error 404: Game not found");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Identify the user from the session
		String userName = findUser(session, gameCmd.getAuthString());
		if (userName == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error 401: Unauthorized");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Determine the current player for the requested color
		String userInGame = (color.equals(ChessGame.TeamColor.WHITE)) ? game.getWhiteUsername() : game.getBlackUsername();
		
		// Check if the color is already taken
		if (!userName.equals(userInGame)) { // userInGame != null &&
			ErrorMessage errorMessage = new ErrorMessage("Error 403: Color already taken");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Check if there are already two players in the game
		if (game.getWhiteUsername() != null && game.getBlackUsername() != null) {
			// Add user as an observer instead
			handleJoinObserverAutomatically(session, gameID, userName);
			return;
		}
		
		// Set the turn to the joining player's color if it's WHITE
		if (color.equals(ChessGame.TeamColor.WHITE)) {
			game.getChessGame().setTeamTurn(ChessGame.TeamColor.WHITE);
		}
		
		if (game.getChessGame() != null) {
			LoadGameMessage loadGameMessage = new LoadGameMessage(game.getChessGame());
			messageDispatcher.sendMessage(session, loadGameMessage);
		} else {
			ErrorMessage errorMessage = new ErrorMessage("Error: Unable to load game data");
			messageDispatcher.sendMessage(session, errorMessage);
		}
		
		// Update the game with the new player
		connectionManager.add(gameCmd.getGameID(), new ConnectionInstance(session, userName));
		gameDAO.updateGame(game);
		
		// After the player joins successfully:
		NotificationMessage notification = new NotificationMessage(userName + " joined as " + color);
		messageDispatcher.broadcastToGameExcept(gameCmd.getGameID(), notification, userName);
	}
	
	// New method to automatically handle joining as an observer
	private void handleJoinObserverAutomatically(Session session, Integer gameID, String userName) throws Exception {
		if (observerManager.isObserver(userName, gameID)) {
			observerManager.add(gameID, new ObserverInstance(session, userName));
			connectionManager.add(gameID, new ConnectionInstance(session, userName));
			
			if (gameDAO.findGameByID(gameID).getChessGame() != null) {
				LoadGameMessage loadGameMessage = new LoadGameMessage(gameDAO.findGameByID(gameID).getChessGame());
				messageDispatcher.sendMessage(session, loadGameMessage);
			} else {
				ErrorMessage errorMessage = new ErrorMessage("Error: Unable to load game data");
				messageDispatcher.sendMessage(session, errorMessage);
			}
			
			NotificationMessage notification = new NotificationMessage(userName + " now observing");
			messageDispatcher.broadcastToGameExceptSession(gameID, notification, session);
		} else {
			ErrorMessage errorMessage = new ErrorMessage(userName + " is already observing game " + gameID);
			messageDispatcher.sendMessage(session, errorMessage);
		}
	}
	
	/**
	 * Handles a player's move request.
	 *
	 * @param session The WebSocket session for the client.
	 * @param gameCmd The command containing details about the move.
	 *
	 * @throws Exception If there's an error in processing the request.
	 */
	private void handleMove(Session session, MakeMoveCommand gameCmd) throws Exception {
		// Find the user associated with the session
		String userName = findUser(session, gameCmd.getAuthString());
		if (userName == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error 401: Unauthorized");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Retrieve the game from the database
		Game game = gameDAO.findGameByID(gameCmd.getGameID());
		if (game == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error 404: Game not found");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Check if the game is already over
		if (game.getChessGame().getTeamTurn() == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error: Game over");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Determine the player's color and check if they can make a move
		ChessGame.TeamColor color = determinePlayerColor(game, userName);
		if (!canPlayerMove(session, game, color, userName)) {
			// canPlayerMove method sends appropriate error messages
			return;
		}
		
		// Attempt to make the move, handling any exceptions
		try {
			game.getChessGame().makeMove(gameCmd.getMove());
		} catch (Exception e) {
			ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Update the game state in the database after a successful move
		gameDAO.updateGame(game);
		
		// Broadcast the updated game state to both players
		LoadGameMessage loadGameMessage = new LoadGameMessage(game.getChessGame());
		messageDispatcher.broadcastToGame(gameCmd.getGameID(), loadGameMessage);
		
		// Prepare and broadcast a notification about the move
		NotificationMessage notification = new NotificationMessage(userName + " made a move: " + gameCmd.getMove().toString());
		messageDispatcher.broadcastToGameExceptSession(gameCmd.getGameID(), notification, session);
	}
	
	/**
	 * Checks if the player can make a move and sends appropriate error messages if not.
	 *
	 * @param session  The WebSocket session for the client.
	 * @param game     The game object.
	 * @param color    The color of the player.
	 * @param userName The username of the player.
	 *
	 * @return True if the player can move, false otherwise.
	 */
	private boolean canPlayerMove(Session session, Game game, ChessGame.TeamColor color, String userName) {
		// Check if the user is an observer, or it's not their turn
		if (color == null || !color.equals(game.getChessGame().getTeamTurn())) {
			ErrorMessage errorMessage = new ErrorMessage("Error: Invalid move"); // "Error: Not your turn"
			messageDispatcher.sendMessage(session, errorMessage);
			return false;
		}
		
		// Check if the user is connected
		if (!connectionManager.getSessionsFromGame(game.getGameID()).containsKey(userName)) {
			ErrorMessage errorMessage = new ErrorMessage("Error: Not connected");
			messageDispatcher.sendMessage(session, errorMessage);
			return false;
		}
		
		// Check if the game is over
		if (game.getChessGame().getTeamTurn() == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error: Game over");
			messageDispatcher.sendMessage(session, errorMessage);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles a player's resignation from a game.
	 *
	 * @param session The WebSocket session for the client.
	 * @param gameCmd The "resign" command received from the client.
	 *
	 * @throws Exception If an error occurs during the process.
	 */
	private void handleResign(Session session, ResignCommand gameCmd) throws Exception {
		GameDAO gameDao = new GameDAO();
		Game game = gameDao.findGameByID(gameCmd.getGameID());
		String userName = findUser(session, gameCmd.getAuthString());
		
		if (userName == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Check if the game is already over
		if (game.getWhiteUsername() == null || game.getBlackUsername() == null) {
			ErrorMessage errorMessage = new ErrorMessage("Error: Game already over");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		} else if (game.getWhiteUsername() != null && game.getWhiteUsername().equals(userName)) {
			game.setWhiteUsername(null);
		} else if (game.getBlackUsername() != null && game.getBlackUsername().equals(userName)) {
			game.setBlackUsername(null);
		} else { // The user is an observer
			ErrorMessage errorMessage = new ErrorMessage("Error: Observer can't resign");
			messageDispatcher.sendMessage(session, errorMessage);
			return;
		}
		
		// Set the game's turn to null to indicate a game end
		game.getChessGame().setTeamTurn(null);
		gameDao.updateGame(game);
		connectionManager.clearGameSessions(gameCmd.getGameID());
		
		// Send a notification to all users about the resignation
		NotificationMessage notification = new NotificationMessage(userName + " resigned");
		messageDispatcher.broadcastToAll(notification);
	}
	
	/**
	 * Handles a player leaving a game.
	 *
	 * @param session The WebSocket session for the client.
	 * @param gameCmd The leave command received from the client.
	 *
	 * @throws Exception If an error occurs during the process.
	 */
	private void handleLeave(Session session, LeaveCommand gameCmd) throws Exception {
		String userName = findUser(session, gameCmd.getAuthString());
		Game game = gameDAO.findGameByID(gameCmd.getGameID());
		
		// Determine the color of the player leaving
		ChessGame.TeamColor color = determinePlayerColor(game, userName);
		if (color == null) {
			observerManager.remove(userName, gameCmd.getGameID()); // Remove as observer
		} else {
			gameDAO.claimSpot(gameCmd.getGameID(), null, color); // Remove from player position
		}
		connectionManager.remove(userName, gameCmd.getGameID()); // Remove from connection manager
		// Send a notification about the player leaving the game
		NotificationMessage notification = new NotificationMessage(userName + " left the game");
		messageDispatcher.broadcastToAll(notification);
	}
	
	/**
	 * Determines the color of a player in a game.
	 *
	 * @param game     The game object.
	 * @param userName The username of the player.
	 *
	 * @return The color of the player, or null if the player is not found.
	 */
	private ChessGame.TeamColor determinePlayerColor(Game game, String userName) {
		if (userName.equals(game.getWhiteUsername())) {
			return ChessGame.TeamColor.WHITE;
		} else if (userName.equals(game.getBlackUsername())) {
			return ChessGame.TeamColor.BLACK;
		} else {
			return null;
		}
	}
	
	/**
	 * Finds the username associated with a given authentication token.
	 *
	 * @param session   The WebSocket session for the client.
	 * @param authToken The authentication token provided by the client.
	 *
	 * @return The username corresponding to the authentication token.
	 *
	 * @throws Exception If the token is invalid or an error occurs.
	 */
	private String findUser(Session session, String authToken) throws Exception {
		try {
			AuthToken auth = new AuthDAO().findAuth(authToken);
			return auth.getUsername();
		} catch (Exception e) {
			ErrorMessage errorMessage = new ErrorMessage("Error 401: unauthorized. Exception: " + e.getMessage());
			messageDispatcher.sendMessage(session, errorMessage);
			throw new Exception("Error 401: unauthorized");
		}
	}
	
	public void clearSessions() {
		connectionManager.removeAll();
		observerManager.removeAll();
	}
}
