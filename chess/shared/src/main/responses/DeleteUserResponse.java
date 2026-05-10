package responses;


/**
 * Represents the response after attempting to delete a user.
 *
 * @param message A message providing success or error info.
 * @param success Indicates the success of the delete operation.
 */
public record DeleteUserResponse(String message, boolean success) implements Response
{
}
