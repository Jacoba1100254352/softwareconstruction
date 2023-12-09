package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand {
    private Integer gameID;

    public JoinObserverCommand(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
        // NOTE: maybe add this.username = username;
    }


    ///   Getters and setters   ///

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
}
