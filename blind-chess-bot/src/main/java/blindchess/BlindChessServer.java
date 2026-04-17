package blindchess;


import blindchess.api.CreateSessionRequest;
import blindchess.api.ErrorResponse;
import blindchess.api.MoveRequest;
import blindchess.api.SessionResponse;
import blindchess.bot.MinimaxChessBot;
import blindchess.model.BlindChessSession;
import blindchess.model.BlindChessSessionManager;
import blindchess.model.GameMode;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import chess.gameplay.ChessGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


public class BlindChessServer
{
	private static final Gson GSON = new Gson();
	private final BlindChessSessionManager sessionManager = new BlindChessSessionManager(new MinimaxChessBot());
	private final HttpServer server;

	public BlindChessServer(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/api/sessions", this::handleSessions);
		server.setExecutor(null);
	}

	public static void main(String[] args) throws IOException {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
		BlindChessServer server = new BlindChessServer(port);
		server.start();
		System.out.println("Blind chess server listening on http://localhost:" + port);
	}

	public void start() {
		server.start();
	}

	private void handleSessions(HttpExchange exchange) throws IOException {
		try {
			List<String> pathSegments = Arrays.stream(exchange.getRequestURI().getPath().split("/"))
					.filter(segment -> !segment.isBlank())
					.toList();
			String method = exchange.getRequestMethod();

			if (pathSegments.size() == 2 && "POST".equals(method)) {
				handleCreateSession(exchange);
				return;
			}

			if (pathSegments.size() == 3) {
				String sessionId = pathSegments.get(2);
				if ("GET".equals(method)) {
					writeJson(exchange, 200, toResponse(sessionManager.getSession(sessionId), null, null));
					return;
				}
			}

			if (pathSegments.size() == 4) {
				String sessionId = pathSegments.get(2);
				String resource = pathSegments.get(3);
				if ("moves".equals(resource) && "POST".equals(method)) {
					handleMove(exchange, sessionId);
					return;
				}
				if ("resign".equals(resource) && "POST".equals(method)) {
					BlindChessSession session = sessionManager.getSession(sessionId);
					session.resignPlayer();
					writeJson(exchange, 200, toResponse(session, null, null));
					return;
				}
			}

			writeJson(exchange, 404, new ErrorResponse("Route not found."));
		} catch (IllegalArgumentException | IllegalStateException e) {
			writeJson(exchange, 400, new ErrorResponse(e.getMessage()));
		} catch (JsonSyntaxException e) {
			writeJson(exchange, 400, new ErrorResponse("Malformed JSON request."));
		} catch (Exception e) {
			writeJson(exchange, 500, new ErrorResponse("Server error: " + e.getMessage()));
		}
	}

	private void handleCreateSession(HttpExchange exchange) throws IOException {
		CreateSessionRequest request = readJson(exchange, CreateSessionRequest.class);
		GameMode mode = GameMode.fromString(request.mode);
		ChessGame.TeamColor playerColor = parseColor(request.playerColor);
		BlindChessSession session = sessionManager.createSession(mode, playerColor);
		writeJson(exchange, 200, toResponse(session, null, null));
	}

	private void handleMove(HttpExchange exchange, String sessionId) throws IOException {
		MoveRequest request = readJson(exchange, MoveRequest.class);
		BlindChessSession session = sessionManager.getSession(sessionId);
		BlindChessSession.TurnOutcome outcome = session.playPlayerMove(request.notation);
		writeJson(exchange, 200, toResponse(session, outcome.playerMove(), outcome.botMove()));
	}

	private ChessGame.TeamColor parseColor(String color) {
		if (color == null) {
			throw new IllegalArgumentException("Player color is required.");
		}
		return ChessGame.TeamColor.valueOf(color.trim().toUpperCase());
	}

	private <T> T readJson(HttpExchange exchange, Class<T> targetClass) throws IOException {
		try (InputStream inputStream = exchange.getRequestBody()) {
			String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			return GSON.fromJson(body, targetClass);
		}
	}

	private SessionResponse toResponse(BlindChessSession session, String playerMove, String botMove) {
		SessionResponse response = new SessionResponse();
		response.sessionId = session.getSessionId();
		response.mode = session.getMode().name();
		response.playerColor = session.getPlayerColor().name();
		response.botColor = session.getBotColor().name();
		response.turn = session.getCurrentTurn() == null ? null : session.getCurrentTurn().name();
		response.yourTurn = session.isPlayerTurn();
		response.status = session.getStatus().name();
		response.resultMessage = session.getResultMessage();
		response.latestMove = session.getLatestMove();
		response.visibleHistory = session.getVisibleHistory();
		response.playerMove = playerMove;
		response.botMove = botMove;
		response.prompt = session.getPrompt();
		return response;
	}

	private void writeJson(HttpExchange exchange, int statusCode, Object payload) throws IOException {
		byte[] json = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
		exchange.sendResponseHeaders(statusCode, json.length);
		try (OutputStream outputStream = exchange.getResponseBody()) {
			outputStream.write(json);
		}
	}
}
