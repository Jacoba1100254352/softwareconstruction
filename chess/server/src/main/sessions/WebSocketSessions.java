package sessions;

import org.eclipse.jetty.websocket.common.WebSocketSession;

import java.util.Map;

public class WebSocketSessions {
    private final Map<Integer, Map<String, WebSocketSession>> sessions;

    public WebSocketSessions(Map<Integer, Map<String, WebSocketSession>> sessions) {
        this.sessions = sessions;
    }

    public void addSessionToGame(Integer gameID, String authToken, WebSocketSession session) {
        sessions.get(gameID).put(authToken, session);
    }

    public void removeSessionFromGame(Integer gameID, String authToken) {
        sessions.get(gameID).remove(authToken);
    }

    public void removeSession(WebSocketSession session) {
        sessions.forEach((gameID, gameSessions) -> gameSessions.remove(session));
    }

    public Map<String, WebSocketSession> getSessions(Integer gameID) {
        return sessions.get(gameID);
    }
}
