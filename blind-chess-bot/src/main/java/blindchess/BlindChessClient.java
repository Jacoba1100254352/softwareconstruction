package blindchess;


import blindchess.api.CreateSessionRequest;
import blindchess.api.ErrorResponse;
import blindchess.api.MoveRequest;
import blindchess.api.SessionResponse;
import blindchess.model.GameMode;
import com.google.gson.Gson;
import chess.gameplay.ChessGame;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;


public class BlindChessClient
{
	private static final Gson GSON = new Gson();
	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final String serverBaseUrl;

	public BlindChessClient(String serverBaseUrl) {
		this.serverBaseUrl = serverBaseUrl;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		String serverBaseUrl = args.length > 0 ? args[0] : "http://localhost:8080";
		new BlindChessClient(serverBaseUrl).run();
	}

	public void run() throws IOException, InterruptedException {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Blind Chess");
		System.out.println("Moves use chess notation such as e4, Nf3, exd5, O-O, or e8=Q.");
		GameMode mode = promptMode(scanner);
		ChessGame.TeamColor color = promptColor(scanner);

		SessionResponse session = post(
				"/api/sessions",
				new CreateSessionRequest(mode.name(), color.name()),
				SessionResponse.class
		);
		printState(session);

		while (true) {
			if (!"ACTIVE".equals(session.status)) {
				return;
			}

			System.out.print("Enter move or command [status, help, resign, quit]: ");
			String input = scanner.nextLine().trim();
			if (input.isEmpty()) {
				continue;
			}

			if (input.equalsIgnoreCase("help")) {
				printHelp(mode);
				continue;
			}
			if (input.equalsIgnoreCase("status")) {
				try {
					session = get("/api/sessions/" + session.sessionId, SessionResponse.class);
					printState(session);
				} catch (IllegalStateException e) {
					System.out.println(e.getMessage());
				}
				continue;
			}
			if (input.equalsIgnoreCase("history")) {
				if (mode == GameMode.NO_HISTORY) {
					System.out.println("History is hidden in no-history mode.");
					continue;
				}
				try {
					session = get("/api/sessions/" + session.sessionId, SessionResponse.class);
					printState(session);
				} catch (IllegalStateException e) {
					System.out.println(e.getMessage());
				}
				continue;
			}
			if (input.equalsIgnoreCase("resign")) {
				try {
					session = post("/api/sessions/" + session.sessionId + "/resign", new Object(), SessionResponse.class);
					printState(session);
				} catch (IllegalStateException e) {
					System.out.println(e.getMessage());
				}
				return;
			}
			if (input.equalsIgnoreCase("quit")) {
				return;
			}
			
			try {
				session = post("/api/sessions/" + session.sessionId + "/moves", new MoveRequest(input), SessionResponse.class);
				printState(session);
			} catch (IllegalStateException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private GameMode promptMode(Scanner scanner) {
		while (true) {
			System.out.print("Choose mode [history/no-history]: ");
			String input = scanner.nextLine().trim();
			try {
				return GameMode.fromString(input);
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private ChessGame.TeamColor promptColor(Scanner scanner) {
		while (true) {
			System.out.print("Play as [white/black]: ");
			String input = scanner.nextLine().trim().toUpperCase();
			try {
				return ChessGame.TeamColor.valueOf(input);
			} catch (IllegalArgumentException e) {
				System.out.println("Please enter white or black.");
			}
		}
	}

	private void printState(SessionResponse session) {
		System.out.println();
		System.out.println("You: " + session.playerColor + " | Bot: " + session.botColor + " | Mode: " + session.mode);

		if ("NO_HISTORY".equals(session.mode)) {
			System.out.println("Latest notation: " + (session.latestMove == null ? "(none)" : session.latestMove));
		} else {
			System.out.println("Move history:");
			if (session.visibleHistory == null || session.visibleHistory.isEmpty()) {
				System.out.println("(none)");
			} else {
				for (String line : session.visibleHistory) {
					System.out.println(line);
				}
			}
			if (session.playerMove != null) {
				System.out.println("You played: " + session.playerMove);
			}
			if (session.botMove != null) {
				System.out.println("Bot replied: " + session.botMove);
			}
		}

		if (session.prompt != null) {
			System.out.println(session.prompt);
		}
		if (session.resultMessage != null && !"ACTIVE".equals(session.status)) {
			System.out.println(session.resultMessage);
		}
		System.out.println();
	}

	private void printHelp(GameMode mode) {
		System.out.println("Commands:");
		System.out.println("status  - reprint the current server state");
		System.out.println("help    - show this command list");
		System.out.println("resign  - resign the current game");
		System.out.println("quit    - leave the client");
		if (mode == GameMode.HISTORY) {
			System.out.println("history - reprint the move list");
		}
	}

	private <T> T get(String path, Class<T> responseType) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(serverBaseUrl + path))
				.GET()
				.build();
		return send(request, responseType);
	}

	private <T> T post(String path, Object body, Class<T> responseType) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(serverBaseUrl + path))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
				.build();
		return send(request, responseType);
	}

	private <T> T send(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() >= 400) {
			ErrorResponse error = GSON.fromJson(response.body(), ErrorResponse.class);
			throw new IllegalStateException(error == null ? "Request failed." : error.error);
		}
		return GSON.fromJson(response.body(), responseType);
	}
}
