package webSocketMessages.serverMessages;


public class ErrorMessage extends ServerMessage
{
	private final String errorMessage;
	
	public ErrorMessage(String errorMessage) {
		super(ServerMessageType.ERROR);
		this.errorMessage = errorMessage;
	}
	
	
	///   Getters and setters   ///
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
