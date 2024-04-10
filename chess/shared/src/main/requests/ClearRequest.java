package requests;


/**
 * Represents the request data required to clear the database.
 *
 * @param authToken The authentication token of the user trying to clear the database.
 */
public record ClearRequest(String authToken)
{
}
