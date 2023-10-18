package services;

/**
 * Represents the request data required for a user to join a game.
 */
public class JoinGameRequest {
    private String gameId;
    private String username;

    /**
     * Default constructor.
     */
    public JoinGameRequest() { }

    /**
     * JoinGameRequest Explicit Constructor.
     *
     * @param gameId The unique ID of the game.
     * @param username The username of the player.
     */
    public JoinGameRequest(String gameId, String username) {
        this.gameId = gameId;
        this.username = username;
    }


    ///   Getters and setters   ///

    /**
     * Retrieves the game ID.
     *
     * @return A string representing the game ID.
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Sets the game ID.
     *
     * @param gameId A string representing the game ID to be set.
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Retrieves the username.
     *
     * @return A string representing the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username A string representing the username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
