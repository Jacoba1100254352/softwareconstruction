package adapters;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.concurrent.CopyOnWriteArrayList;

public class ChessWebSocketAdapter extends WebSocketAdapter {
    private final CopyOnWriteArrayList<Session> clientSessions = new CopyOnWriteArrayList<>();

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        clientSessions.add(sess);
        System.out.println("WebSocket connection established with " + sess.getRemoteAddress().getAddress());
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        clientSessions.remove(getSession());
        System.out.println("WebSocket connection closed: " + statusCode + " - " + reason);
    }

    // Implement other necessary methods
}
