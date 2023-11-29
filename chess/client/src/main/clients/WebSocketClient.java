package clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class WebSocketClient {
    private WebSocket webSocket;

    public interface MessageHandler {
        void handleMessage(String message);
    }

    private final MessageHandler messageHandler;

    public WebSocketClient(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void connectToWebSocket(String serverUri) {
        HttpClient client = HttpClient.newHttpClient();
        WebSocket.Builder builder = client.newWebSocketBuilder();

        webSocket = builder.buildAsync(URI.create(serverUri), new WebSocket.Listener() {
            @Override
            public void onOpen(WebSocket webSocket) {
                System.out.println("WebSocket opened");
                WebSocket.Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                // Use the message handler to process the received message
                messageHandler.handleMessage(data.toString());
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                System.out.println("WebSocket closed, Status: " + statusCode + ", Reason: " + reason);
                WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
                return null;
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                System.err.println("WebSocket error: " + error.getMessage());
                WebSocket.Listener.super.onError(webSocket, error);
            }
        }).join();
    }



    public void sendMessage(String message) {
        webSocket.sendText(message, true);
    }

    public void closeConnection() {
        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing connection").thenRun(() -> System.out.println("WebSocket closed"));
    }
}
