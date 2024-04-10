package requests;


/**
 * Represents the request data required to list all games.
 *
 * @param authToken The authentication token of the user trying to list games.
 */
public record ListGamesRequest(String authToken)
{
}
