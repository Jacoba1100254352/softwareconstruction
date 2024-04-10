package webSocketMessages.serverMessages;


import chess.gameplay.ChessGame;


public class LoadGameMessage extends ServerMessage
{
	ChessGame game;
	
	public LoadGameMessage(ChessGame game) {
		super(ServerMessageType.LOAD_GAME);
		this.game = game;
	}
	
	
	///   Getters and setters   ///
	
	public ChessGame getGame() {
		return game;
	}
}
