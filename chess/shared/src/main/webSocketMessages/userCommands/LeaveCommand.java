package webSocketMessages.userCommands;


public class LeaveCommand extends UserGameCommand
{
	private final Integer gameID;
	
	public LeaveCommand(String authToken, Integer gameID) {
		super(authToken);
		this.commandType = CommandType.LEAVE;
		this.gameID = gameID;
	}
	
	
	///   Getters and setters   ///
	
	public Integer getGameID() {
		return gameID;
	}
}
