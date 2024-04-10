package webSocketManagement;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;


public class MessageDispatcher
{
	private final ConnectionManager connectionManager;
	private final boolean verbose;
	private final Gson gson;
	
	public MessageDispatcher(ConnectionManager connectionManager, boolean verbose) {
		this.connectionManager = connectionManager;
		this.gson = new Gson();
		this.verbose = verbose;
	}
	
	public void sendMessage(Session session, ServerMessage message) {
		String jsonMessage = gson.toJson(message);
		
		if (verbose) {
			System.out.println(getCurrentMethodName() + " function sends message: " + jsonMessage);
		}
		
		try {
			if (session != null && session.isOpen()) {
				session.getRemote().sendString(jsonMessage);
			}
		} catch (IOException e) {
			System.err.println("Error sending message: " + e.getMessage());
		}
	}
	
	/**
	 * Broadcasts a message to all sessions in a game, except a specified session.
	 *
	 * @param gameID        The ID of the game.
	 * @param message       The message to broadcast.
	 * @param exceptSession The session to exclude from broadcasting.
	 */
	public void broadcastToGameExceptSession(Integer gameID, ServerMessage message, Session exceptSession) {
		String jsonMessage = gson.toJson(message);
		
		if (verbose) {
			System.out.println(getCurrentMethodName() + " function sends message: " + jsonMessage);
		}
		
		connectionManager.getSessionsFromGame(gameID).forEach((username, clientSession) -> {
			if (clientSession != null && clientSession.isOpen() && (!clientSession.equals(exceptSession))) {
				try {
					clientSession.getRemote().sendString(jsonMessage);
				} catch (IOException e) {
					System.err.println("Error broadcasting to game: " + e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Broadcasts a message to all sessions associated with a game.
	 *
	 * @param gameID  The ID of the game.
	 * @param message The message to broadcast.
	 */
	public void broadcastToGame(Integer gameID, ServerMessage message) {
		String jsonMessage = gson.toJson(message);
		
		if (verbose) {
			System.out.println(getCurrentMethodName() + " function sends message: " + jsonMessage);
		}
		
		connectionManager.getSessionsFromGame(gameID).forEach((username, session) -> {
			if (session != null && session.isOpen()) {
				try {
					session.getRemote().sendString(jsonMessage);
				} catch (IOException e) {
					System.err.println("Error broadcasting to game: " + e.getMessage());
				}
			}
		});
	}
	
	public void broadcastToGameExcept(Integer gameID, ServerMessage message, String userExcluded) {
		String jsonMessage = gson.toJson(message);
		
		if (verbose) {
			System.out.println(getCurrentMethodName() + " function sends message: " + jsonMessage);
		}
		
		connectionManager.getSessionsFromGame(gameID).forEach((username, session) -> {
			if (!username.equals(userExcluded) && session != null && session.isOpen()) {
				try {
					session.getRemote().sendString(jsonMessage);
				} catch (IOException e) {
					System.err.println("Error in broadcastToGameExcept: " + e.getMessage());
				}
			}
		});
	}
	
	public void broadcastToAll(ServerMessage message) {
		String jsonMessage = gson.toJson(message);
		if (verbose) {
			System.out.println(getCurrentMethodName() + " function sends message: " + jsonMessage);
		}
		
		connectionManager.getAllSessions().forEach(session -> {
			if (session.isOpen()) {
				try {
					session.getRemote().sendString(jsonMessage);
				} catch (IOException e) {
					System.err.println("Error broadcasting to session: " + e.getMessage());
				}
			}
		});
	}
	
	private String getCurrentMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
}
