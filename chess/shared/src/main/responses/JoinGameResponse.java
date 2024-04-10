package responses;


/**
 * Represents the result of a join game request.
 *
 * @param message A message providing details or an error description.
 * @param success Indicates the success of the join operation.
 */
public record JoinGameResponse(String message, boolean success) implements Response
{
}
