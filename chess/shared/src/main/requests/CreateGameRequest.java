package requests;


/**
 * Represents the request data required to create a new game.
 *
 * @param authToken The authentication token of the user trying to create a game.
 * @param gameName  The name of the game to be created.
 */
public record CreateGameRequest(String authToken, String gameName)
{
}
