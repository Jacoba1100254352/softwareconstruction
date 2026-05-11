import dataAccess.Database;
import main.Server;
import testFactory.TestFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class TestServerSupport
{
	private static Server server;
	private static boolean startedHere;

	private TestServerSupport() {
	}

	public static void useInMemoryDatabase() {
		Database.useInMemoryStoreForTests();
	}

	public static synchronized void startServer() {
		useInMemoryDatabase();
		if (isServerListening()) {
			startedHere = false;
			return;
		}

		server = new Server();
		server.start();
		startedHere = true;
	}

	public static synchronized void stopServer() {
		if (startedHere && server != null) {
			server.stopServer();
		}
		server = null;
		startedHere = false;
	}

	public static synchronized void restartServer() {
		if (startedHere && server != null) {
			server.stopServer();
			server = new Server();
			server.start();
		}
	}

	private static boolean isServerListening() {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress("localhost", Integer.parseInt(TestFactory.getServerPort())), 200);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
