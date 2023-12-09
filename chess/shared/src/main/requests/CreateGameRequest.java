package requests;

/**
 * Represents the request data required to create a new game.
 */
public class CreateGameRequest {

    /**
     * The authentication token of the user trying to create a game.
     */
    private String authToken;

    /**
     * The name of the game to be created.
     */
    private String gameName;


    ///   Constructor   ///

    /**
     * Constructor for the created game's data.
     *
     * @param authToken The authentication token of the user.
     * @param gameName  The name of the game.
     */
    public CreateGameRequest(String authToken, String gameName) {
        this.authToken = authToken;
        this.gameName = gameName;
    }


    ///   Getters and setters   ///

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
