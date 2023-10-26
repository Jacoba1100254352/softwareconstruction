package models;

import chess.ChessGame;

/**
 * Represents a game with associated attributes and players.
 */
public class Game {
    /**
     * The unique ID of the game.
     */
    private int gameID;
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

    /**
     * Default constructor.
     */
    public Game() { }

    /**
     * Constructs a game with a given ID and name.
     * Other attributes can be set later.
     *
     * @param gameID The unique ID of the game.
     * @param gameName The name of the game.
     */
    public Game(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = "";  // or null
        this.blackUsername = "";  // or null
        this.game = null;
    }


    /**
     * Constructs a game with the specified values.
     *
     * @param gameID The unique ID of the game.
     * @param whiteUsername The white player's username.
     * @param blackUsername The black player's username.
     * @param gameName The name of the game.
     * @param game The chess game.
     */
    public Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }


    ///   Getters and setters   ///

    /**
     * Gets the game ID.
     *
     * @return The game ID.
     */
    public int getGameID() {
        return this.gameID;
    }

    /**
     * Sets the game ID.
     *
     * @param gameID The game ID to be set.
     */
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Retrieves the username of the white player in the game.
     *
     * @return The username of the white player.
     */
    public String getWhiteUsername() {
        return whiteUsername;
    }

    /**
     * Sets the username for the white player in the game.
     *
     * @param whiteUsername The username to be set for the white player.
     */
    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    /**
     * Retrieves the username of the black player in the game.
     *
     * @return The username of the black player.
     */
    public String getBlackUsername() {
        return blackUsername;
    }

    /**
     * Sets the username for the black player in the game.
     *
     * @param blackUsername The username to be set for the black player.
     */
    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    /**
     * Retrieves the name of the game.
     *
     * @return The game's name.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Sets the name for the game.
     *
     * @param gameName The name to be set for the game.
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Retrieves the current state or implementation of the chess game.
     *
     * @return An instance of the ChessGame representing the game's state.
     */
    public ChessGame getGame() {
        return game;
    }

    /**
     * Sets the current state or implementation for the chess game.
     *
     * @param game An instance of ChessGame to be set as the game's current state.
     */
    public void setGame(ChessGame game) {
        this.game = game;
    }
}
