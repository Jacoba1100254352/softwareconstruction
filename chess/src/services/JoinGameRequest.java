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
    private String gameId;
    /**
     * The username of the user.
     */
    private String username;
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
     * @param gameId The unique ID of the game.
     * @param username The username of the user.
     * @param playerColor The color the player wishes to play as.
     */
    public JoinGameRequest(String gameId, String username, String playerColor) {
        this.gameId = gameId;
        this.username = username;
        this.playerColor = playerColor;
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the game ID.
     *
     * @return The game ID.
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Sets the game ID.
     *
     * @param gameId The game ID to be set.
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Retrieves the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username The username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
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
