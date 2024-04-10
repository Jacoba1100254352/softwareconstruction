package webSocketMessages.userCommands;


import chess.gameplay.ChessGame;


public class JoinPlayerCommand extends UserGameCommand
{
	private final Integer gameID;
	private final ChessGame.TeamColor playerColor;
	
	public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
		super(authToken);
		this.commandType = CommandType.JOIN_PLAYER;
		this.gameID = gameID;
		this.playerColor = playerColor;
	}
	
	
	///   Getters and setters   ///
	
	public Integer getGameID() {
		return gameID;
	}
	
	public ChessGame.TeamColor getPlayerColor() {
		return playerColor;
	}
}
