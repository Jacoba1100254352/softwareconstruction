package WebSocketFacade;

import chess.ChessGame;
import clients.ChessClient;
import clients.WebSocketClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.logging.Logger;

public class WebSocketFacade extends Endpoint {
    private static final Logger LOGGER = Logger.getLogger(WebSocketFacade.class.getName());
    private final ChessClient chessClient;
    private final WebSocketClient webSocketClient;
    private final Gson gson = new Gson();
    private Session session;

    public WebSocketFacade(ChessClient chessClient, WebSocketClient webSocketClient) {
        this.chessClient = chessClient;
        this.webSocketClient = webSocketClient;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        LOGGER.info("WebSocket connection opened: " + config.toString());
        this.session = session;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.info("WebSocket connection closed: " + closeReason.getReasonPhrase());
        webSocketClient.notifyUser("Connection closed: " + closeReason.getReasonPhrase());
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        LOGGER.severe("WebSocketFacade error: " + throwable.getMessage());
        webSocketClient.notifyUser("Error: " + throwable.getMessage());
    }

    public void connect(String uri) throws WebSocketFacadeException {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, new URI(uri));
            LOGGER.info("Connected to server");
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @OnMessage
                public void onMessage(String message) {
                    LOGGER.info("Raw message received: " + message);
                    try {
                        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                        String messageType = jsonObject.get("serverMessageType").getAsString();

                        switch (messageType) {
                            case "LOAD_GAME":
                                handleLoadGame(message);
                                break;
                            case "ERROR":
                                handleError(message);
                                break;
                            case "NOTIFICATION":
                                handleNotification(message);
                                break;
                            default:
                                LOGGER.warning("Unknown message type received: " + messageType);
                        }
                    } catch (JsonSyntaxException e) {
                        LOGGER.severe("Invalid JSON message format: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            throw new WebSocketFacadeException("Error connecting to server: " + uri, e);
        }
    }

    public void sendMessage(String message) throws WebSocketFacadeException {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                LOGGER.info("Message sent: " + message);
            } catch (Exception e) {
                throw new WebSocketFacadeException("Error sending message: " + message, e);
            }
        } else {
            throw new WebSocketFacadeException("Session is not open or does not exist", null);
        }
    }

    private void handleLoadGame(String message) {
        LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
        ChessGame updatedGame = loadGameMessage.getGame();
        chessClient.getGameplayUI().redraw(updatedGame, null, null);
    }

    private void handleError(String message) {
        ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
        LOGGER.info("Error received: " + errorMessage.getErrorMessage());
        chessClient.getGameplayUI().displayError(errorMessage.getErrorMessage());
    }

    private void handleNotification(String message) {
        NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
        LOGGER.info("Notification: " + notificationMessage.getNotificationMessage());
        chessClient.getGameplayUI().showNotification(notificationMessage.getNotificationMessage());
    }
}
