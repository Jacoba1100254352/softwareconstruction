package requests;

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
    private Integer gameID;
    /**
     * The color the player wishes to play as.
     */
    private String playerColor;

    /**
     * Default constructor.
     */
    public JoinGameRequest() {
    }

    /**
     * JoinGameRequest Explicit Constructor.
     *
     * @param authToken   The authentication token of the user.
     * @param gameID      The unique ID of the game.
     * @param playerColor The color the player wishes to play as.
     */
    public JoinGameRequest(String authToken, Integer gameID, String playerColor) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the game ID.
     *
     * @return The game ID.
     */
    public Integer getGameID() {
        return gameID;
    }

    /**
     * Sets the game ID.
     *
     * @param gameID The game ID to be set.
     */
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
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
