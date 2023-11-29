package clients;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import javax.websocket.ContainerProvider;

@ClientEndpoint
public class WebSocketClient {

    private Session userSession = null;
    private MessageHandler messageHandler;

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnMessage
    public void onMessage(String message) {
        if (messageHandler != null) {
            messageHandler.handleMessage(message);
        }
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public interface MessageHandler {
        void handleMessage(String message);
    }
}
