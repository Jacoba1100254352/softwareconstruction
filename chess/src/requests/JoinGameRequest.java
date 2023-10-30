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


    ///   Constructor   ///

    /**
     * Constructor for the user's join game data.
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

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

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
