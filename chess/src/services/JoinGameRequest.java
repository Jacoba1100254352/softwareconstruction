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
     * @param username The username of the user.
     */
    public JoinGameRequest(String gameId, String username) {
        this.gameId = gameId;
        this.username = username;
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
}
