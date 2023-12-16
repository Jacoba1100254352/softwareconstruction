package requests;

/**
 * Represents the request data required for a user to join a game.
 *
 * @param authToken   The authentication token of the user trying to join a game.
 * @param gameID      The unique ID of the game.
 * @param playerColor The color the player wishes to play as.
 */
public record JoinGameRequest(String authToken, Integer gameID, String playerColor) {
}
