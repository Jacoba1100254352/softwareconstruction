package blindchess.api;


import java.util.List;


public class SessionResponse
{
	public boolean success = true;
	public String sessionId;
	public String mode;
	public String playerColor;
	public String botColor;
	public String turn;
	public boolean yourTurn;
	public String status;
	public String resultMessage;
	public String latestMove;
	public List<String> visibleHistory;
	public String playerMove;
	public String botMove;
	public String prompt;
}
