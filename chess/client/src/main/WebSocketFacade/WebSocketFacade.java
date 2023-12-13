package WebSocketFacade;

import javax.websocket.*;
import java.net.URI;
import java.util.logging.Logger;

import chess.ChessGame;
import clients.*;
import com.google.gson.*;
import webSocketMessages.serverMessages.*;

public class WebSocketFacade extends Endpoint {
    private Session session;
    private final ChessClient chessClient;
    private final WebSocketClient webSocketClient;
    private static final Logger LOGGER = Logger.getLogger(WebSocketFacade.class.getName());

    public WebSocketFacade(ChessClient chessClient, WebSocketClient webSocketClient) {
        System.out.println("WebSocketFacade constructor");
        this.chessClient = chessClient;
        this.webSocketClient = webSocketClient;
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
        webSocketClient.notifyUser("Connection closed: " + closeReason.getReasonPhrase());
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        LOGGER.severe("WebSocket error: " + throwable.getMessage());

        // Notify ChessClient about the error
        webSocketClient.notifyUser("Error: " + throwable.getMessage());
    }

    public void connect(String uri) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, new URI(uri));

        System.out.println("Connected to server");
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @OnMessage
            public void onMessage(String message) {
                System.out.println("Raw message received: " + message);

                ServerMessage serverMessage = (new Gson()).fromJson(message, ServerMessage.class);

                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME:
                        handleLoadGame(message);
                        break;
                    case ERROR:
                        handleError(message);
                        break;
                    case NOTIFICATION:
                        handleNotification(message);
                        break;
                }
            }
        });
    }

    private void handleLoadGame(String message) {
        LoadGameMessage loadGameMessage = (new Gson()).fromJson(message, LoadGameMessage.class);
        ChessGame updatedGame = loadGameMessage.getGame();
        chessClient.getGameplayUI().redraw(updatedGame, null, null);
    }

    private void handleError(String message) {
        ErrorMessage errorMessage = (new Gson()).fromJson(message, ErrorMessage.class);
        System.out.println("Error received: " + errorMessage.getErrorMessage());
        chessClient.getGameplayUI().displayError(errorMessage.getErrorMessage());
    }

    private void handleNotification(String message) {
        NotificationMessage notificationMessage = (new Gson()).fromJson(message, NotificationMessage.class);
        System.out.println("Notification: " + notificationMessage.getNotificationMessage());
        chessClient.getGameplayUI().showNotification(notificationMessage.getNotificationMessage());
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("Message sent: " + message);
            } catch (Exception e) {
                LOGGER.severe("Error sending message: " + e.getMessage());
                // Notify ChessClient about the message sending error
                webSocketClient.notifyUser("Error sending message: " + e.getMessage());
            }
        }
    }
}
