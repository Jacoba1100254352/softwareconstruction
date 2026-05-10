package requests;


/**
 * Represents the request data required to delete a user.
 *
 * @param authToken The authentication token of the user trying to delete the specified user.
 * @param username  The username of the user to be deleted.
 */
public record DeleteUserRequest(String authToken, String username)
{
}
