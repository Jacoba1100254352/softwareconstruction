package webSocketManagement;

import org.eclipse.jetty.websocket.api.Session;

public interface ClientInstance {
    Session getSession();

    String getUsername();
}
