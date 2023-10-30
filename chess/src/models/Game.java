package models;

import chess.ChessGame;

/**
 * Represents a game with associated attributes and players.
 */
public class Game {
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
     * The name of the game.
     */
    private String gameName;
    /**
     * The chess game.
     */
    private ChessGame game;


    ///   Constructor   ///

    /**
     * Constructor for a new game with the given attributes. (Other attributes can be set later)
     *
     * @param gameID   The unique ID of the game.
     * @param gameName The name of the game.
     */
    public Game(Integer gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = null;
        this.blackUsername = null;
        this.game = null;
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

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
}
