package servers;

import java.net.http.WebSocket;


import java.net.InetSocketAddress;

public class WebSocketServer {

    /*public WebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }*/

    /*
    // ClientHandshake doesn't exist
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        System.out.println("New connection opened");
    }
    */

    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection");
    }

    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        // Handle messages here
    }

    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred:" + ex.getMessage());
    }

    public void onStart() {
        System.out.println("Server started successfully");
    }
}
