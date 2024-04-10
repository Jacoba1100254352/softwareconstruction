package clients;


import serverFacade.ServerFacade;
import testFactory.TestFactory;
import ui.GameplayUI;
import ui.PostloginUI;
import ui.PreloginUI;


public class ChessClient
{
	private final ServerFacade serverFacade;
	private final PreloginUI preloginUI;
	private final GameplayUI gameplayUI;
	private PostloginUI postloginUI;
	private String authToken;
	
	private boolean isRunning;
	private boolean isLoggedIn;
	
	public ChessClient() {
		serverFacade = new ServerFacade("http://localhost:" + TestFactory.getServerPort());
		
		preloginUI = new PreloginUI(this, serverFacade);
		// The postloginUI variable is initialized when transitioning to "logged in" when the chessClient has more info
		postloginUI = null;
		gameplayUI = new GameplayUI();
		
		isRunning = true;
	}
	
	public static void main(String[] args) {
		(new ChessClient()).run();
	}
	
	public void run() {
		while (isRunning) {
			if (isLoggedIn) {
				postloginUI.displayMenu();
			} else {
				preloginUI.displayMenu();
			}
		}
	}
	
	public void exit() {
		isRunning = false;
	}
	
	public GameplayUI getGameplayUI() {
		return gameplayUI;
	}
	
	public void transitionToPostloginUI() {
		postloginUI = new PostloginUI(this, serverFacade);
		isLoggedIn = true;
	}
	
	public void transitionToPreloginUI() {
		isLoggedIn = false;
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public void setAuthToken(String token) {
		this.authToken = token;
	}
	
	public boolean isDebugMode() {
		return false;
	}
}
