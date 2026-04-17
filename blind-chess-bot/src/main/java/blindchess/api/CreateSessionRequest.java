package blindchess.api;


public class CreateSessionRequest
{
	public String mode;
	public String playerColor;

	public CreateSessionRequest() {
	}

	public CreateSessionRequest(String mode, String playerColor) {
		this.mode = mode;
		this.playerColor = playerColor;
	}
}
