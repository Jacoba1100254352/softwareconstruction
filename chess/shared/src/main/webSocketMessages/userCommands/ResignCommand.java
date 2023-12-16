package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    private final Integer gameID;

    public ResignCommand(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }


    ///   Getters and setters   ///

    public Integer getGameID() {
        return gameID;
    }
}
