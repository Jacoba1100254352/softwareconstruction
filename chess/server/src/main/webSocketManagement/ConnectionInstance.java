package webSocketManagement;


import org.eclipse.jetty.websocket.api.Session;


public class ConnectionInstance implements ClientInstance
{
	private final Session session;
	private final String userName;
	
	public ConnectionInstance(Session session, String userName) {
		this.session = session;
		this.userName = userName;
	}
	
	@Override
	public Session getSession() {
		return session;
	}
	
	@Override
	public String getUsername() {
		return userName;
	}
}
