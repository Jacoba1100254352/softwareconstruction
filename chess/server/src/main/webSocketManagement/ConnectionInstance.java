package webSocketManagement;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;

public class ConnectionInstance {
    public Session session;
    public String userName;
    // public Integer gameId; // Uncomment if game ID is needed in the instance

    public ConnectionInstance(Session session, String userName) {
        this.session = session;
        this.userName = userName;
        // this.gameId = gameId; // Uncomment if game ID is needed in the instance
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public Session getSession() {
        return session;
    }
}
