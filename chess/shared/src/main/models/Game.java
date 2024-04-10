package models;


import chess.gameplay.ChessGame;


/**
 * Represents a game with associated attributes and players.
 */
public class Game
{
	/**
	 * The name of the game.
	 */
	private final String gameName;
	/**
	 * The unique ID of the game.
	 */
	private Integer gameID;
	/**
	 * The white player's username.
	 */
	private String whiteUsername;
	/**
	 * The black player's username.
	 */
	private String blackUsername;
	/**
	 * The chess game.
	 */
	private ChessGame chessGame;
	
	
	///   Constructor   ///
	
	/**
	 * Constructor for a new game with the given attributes.
	 *
	 * @param gameID    The unique ID of the game.
	 * @param gameName  The name of the game.
	 * @param chessGame The Chess Game object.
	 */
	public Game(Integer gameID, String gameName, ChessGame chessGame) {
		this.gameID = gameID;
		this.gameName = gameName;
		this.whiteUsername = null;
		this.blackUsername = null;
		this.chessGame = chessGame;
	}
	
	/**
	 * Constructor for a new game with the given attributes.
	 *
	 * @param gameID        The unique ID of the game.
	 * @param gameName      The name of the game.
	 * @param whiteUsername The username of the white player.
	 * @param blackUsername The username of the black player.
	 * @param chessGame     The Chess Game object.
	 */
	public Game(Integer gameID, String gameName, String whiteUsername, String blackUsername, ChessGame chessGame) {
		this.gameID = gameID;
		this.gameName = gameName;
		this.whiteUsername = whiteUsername;
		this.blackUsername = blackUsername;
		this.chessGame = chessGame;
	}
	
	
	///   Getters and setters   ///
	
	public Integer getGameID() {
		return this.gameID;
	}
	
	public void setGameID(Integer gameID) {
		this.gameID = gameID;
	}
	
	public String getWhiteUsername() {
		return whiteUsername;
	}
	
	public void setWhiteUsername(String whiteUsername) {
		this.whiteUsername = whiteUsername;
	}
	
	public String getBlackUsername() {
		return blackUsername;
	}
	
	public void setBlackUsername(String blackUsername) {
		this.blackUsername = blackUsername;
	}
	
	public String getGameName() {
		return gameName;
	}
	
	public ChessGame getChessGame() {
		return chessGame;
	}
	
	public void setGame(ChessGame chessGame) {
		this.chessGame = chessGame;
	}
}
