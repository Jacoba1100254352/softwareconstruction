package blindchess.api;


public class ErrorResponse
{
	public boolean success = false;
	public String error;

	public ErrorResponse(String error) {
		this.error = error;
	}
}
