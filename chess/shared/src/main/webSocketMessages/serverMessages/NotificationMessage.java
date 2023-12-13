package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.message = notificationMessage;
    }


    ///   Getters and setters   ///

    public String getNotificationMessage() {
        return message;
    }
}
