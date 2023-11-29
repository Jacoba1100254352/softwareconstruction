package webSocketMessages.serverMessages;

public class LoadGameMessage extends ServerMessage {
    private String loadGameMessage;

    public LoadGameMessage(String loadGameMessage) {
        super(ServerMessageType.LOAD_GAME);
        this.loadGameMessage = loadGameMessage;
    }


    ///   Getters and setters   ///

    public String getLoadGameMessage() {
        return loadGameMessage;
    }

    public void setLoadGameMessage(String loadGameMessage) {
        this.loadGameMessage = loadGameMessage;
    }
}
