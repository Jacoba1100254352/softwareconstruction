package responses;


/**
 * Represents the response after attempting to clear the database.
 *
 * @param message A message providing success or error info.
 * @param success Indicates the success of the clear operation.
 */
public record ClearResponse(String message, boolean success) implements Response
{
}
