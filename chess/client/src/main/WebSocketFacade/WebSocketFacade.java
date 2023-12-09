package WebSocketFacade;

import javax.websocket.*;
import java.net.URI;
import java.util.logging.Logger;

import clients.ChessClient;
import com.google.gson.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

public class WebSocketFacade extends Endpoint {
    private Session session;
    private final ChessClient chessClient; // Reference to ChessClient
    private static final Logger LOGGER = Logger.getLogger(WebSocketFacade.class.getName());

    public WebSocketFacade(ChessClient chessClient) {
        this.chessClient = chessClient;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        LOGGER.info("WebSocket connection opened: " + config.toString());
        // Initialize the session
        this.session = session;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.info("WebSocket connection closed: " + closeReason.getReasonPhrase());
        // Notify ChessClient about the closure
        chessClient.notifyUser("Connection closed: " + closeReason.getReasonPhrase());
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        LOGGER.severe("WebSocket error: " + throwable.getMessage());
        // Notify ChessClient about the error
        chessClient.notifyUser("Error: " + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message) {
        Gson gson = new Gson();
        JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
        String messageType = jsonMessage.get("serverMessageType").getAsString();

        switch (ServerMessage.ServerMessageType.valueOf(messageType)) {
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = gson.fromJson(jsonMessage, LoadGameMessage.class);
                chessClient.getGameplayUI().updateGameState(loadGameMessage.getLoadGameMessage());
                break;

            case ERROR:
                ErrorMessage errorMessage = gson.fromJson(jsonMessage, ErrorMessage.class);
                System.out.println("Error received: " + errorMessage.getErrorMessage());
                chessClient.getGameplayUI().displayError(errorMessage.getErrorMessage());
                break;

            case NOTIFICATION:
                NotificationMessage notificationMessage = gson.fromJson(jsonMessage, NotificationMessage.class);
                System.out.println("Notification: " + notificationMessage.getNotificationMessage());
                chessClient.getGameplayUI().showNotification(notificationMessage.getNotificationMessage());
                break;
        }
    }

    public void connect(String uri) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, new URI(uri));
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                LOGGER.severe("Error sending message: " + e.getMessage());
                // Notify ChessClient about the message sending error
                chessClient.notifyUser("Error sending message: " + e.getMessage());
            }
        }
    }
}
