package responses;


/**
 * Represents the result of a registration request.
 *
 * @param authToken The authentication token for the registered user.
 * @param username  The username of the registered user.
 * @param message   The error message.
 * @param success   Indicates the success of the register operation.
 */
public record RegisterResponse(String authToken, String username, String message, boolean success) implements Response
{
}
