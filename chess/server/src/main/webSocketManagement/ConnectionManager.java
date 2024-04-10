package webSocketManagement;


import org.eclipse.jetty.websocket.api.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ConnectionManager implements ClientManager
{
	private final ConcurrentHashMap<String, ConnectionInstance> connections;
	private final ConcurrentHashMap<Integer, Set<ConnectionInstance>> gameSessions;
	
	public ConnectionManager() {
		connections = new ConcurrentHashMap<>();
		gameSessions = new ConcurrentHashMap<>();
	}
	
	@Override
	public void add(Integer gameID, ClientInstance instance) {
		try {
			if (instance instanceof ConnectionInstance connectionInstance) {
				// Update connection map
				connections.put(connectionInstance.getUsername(), connectionInstance);
				// Update game session map
				gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(connectionInstance);
			}
		} catch (Exception e) {
			System.err.println("Error adding user: " + e.getMessage());
		}
	}
	
	@Override
	public void remove(String username, Integer gameID) {
		try {
			ConnectionInstance instance = connections.remove(username);
			if (instance != null && gameID != null) {
				gameSessions.computeIfPresent(gameID, (k, v) -> {
					v.remove(instance);
					return v.isEmpty() ? null : v;
				});
			}
		} catch (Exception e) {
			System.err.println("Error removing user: " + e.getMessage());
		}
	}
	
	@Override
	public void removeAll() {
		connections.clear();
		gameSessions.clear();
	}
	
	public Map<String, Session> getSessionsFromGame(Integer gameID) {
		Map<String, Session> sessions = new HashMap<>();
		Set<ConnectionInstance> gameSet = gameSessions.get(gameID);
		if (gameSet != null) {
			for (ConnectionInstance instance : gameSet) {
				sessions.put(instance.getUsername(), instance.getSession());
			}
		}
		return sessions;
	}
	
	public Collection<Session> getAllSessions() {
		Collection<Session> allSessions = new ArrayList<>();
		try {
			allSessions = connections.values().stream().map(ConnectionInstance::getSession).collect(Collectors.toList());
		} catch (Exception e) {
			System.err.println("Error getting all sessions: " + e.getMessage());
		}
		
		return allSessions;
	}
	
	// Call this method when a game ends to clear the game sessions
	public void clearGameSessions(Integer gameID) {
		gameSessions.remove(gameID);
	}
}
