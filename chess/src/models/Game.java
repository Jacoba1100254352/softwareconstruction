package models;

import chess.ChessGame;

/**
 * Represents a game with associated attributes and players.
 */
public class Game {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;

    /**
     * Default constructor.
     */
    public Game() { }

    /**
     * Constructs a game with the specified values.
     *
     * @param gameID The game ID.
     * @param whiteUsername The white player's username.
     * @param blackUsername The black player's username.
     * @param gameName The name of the game.
     */
    public Game(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
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
