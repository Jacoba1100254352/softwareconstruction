package services;

/**
 * Represents the request data required for a user to join a game.
 */
public class JoinGameRequest {
    /**
     * The authentication token of the user trying to join a game.
     */
    private String authToken;
    /**
     * The unique ID of the game.
     */
    private int gameId;
    /**
     * The color the player wishes to play as.
     */
    private String playerColor;

    /**
     * Default constructor.
     */
    public JoinGameRequest() { }

    /**
     * JoinGameRequest Explicit Constructor.
     *
     * @param authToken The authentication token of the user.
     * @param gameId The unique ID of the game.
     * @param playerColor The color the player wishes to play as.
     */
    public JoinGameRequest(String authToken, int gameId, String playerColor) {
        this.authToken = authToken;
        this.gameId = gameId;
        this.playerColor = playerColor;
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the game ID.
     *
     * @return The game ID.
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Sets the game ID.
     *
     * @param gameId The game ID to be set.
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /**
     * Retrieves the player's color.
     *
     * @return The player's color.
     */
    public String getPlayerColor() {
        return playerColor;
    }

    /**
     * Sets the player's color.
     *
     * @param playerColor The color to be set.
     */
    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
