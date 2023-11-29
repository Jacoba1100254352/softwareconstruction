package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    private String notificationMessage;

    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.ERROR);
        this.notificationMessage = notificationMessage;
    }


    ///   Getters and setters   ///

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }
}
