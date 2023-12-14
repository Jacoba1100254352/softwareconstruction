package responses;

/**
 * Represents the response after attempting to create a game.
 *
 * @param gameID  The unique ID of the created game.
 * @param message A message providing success or error info.
 * @param success Indicates the success of the create game operation.
 */
public record CreateGameResponse(Integer gameID, String message, boolean success) implements Response {
}
