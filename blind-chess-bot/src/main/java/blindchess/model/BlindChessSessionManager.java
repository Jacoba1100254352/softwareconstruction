package blindchess.model;


import blindchess.bot.ChessBot;
import chess.gameplay.ChessGame;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class BlindChessSessionManager
{
	private final Map<String, BlindChessSession> sessions = new ConcurrentHashMap<>();
	private final ChessBot bot;

	public BlindChessSessionManager(ChessBot bot) {
		this.bot = bot;
	}

	public BlindChessSession createSession(GameMode mode, ChessGame.TeamColor playerColor) {
		String sessionId = UUID.randomUUID().toString();
		BlindChessSession session = new BlindChessSession(sessionId, mode, playerColor, bot);
		sessions.put(sessionId, session);
		return session;
	}

	public BlindChessSession getSession(String sessionId) {
		BlindChessSession session = sessions.get(sessionId);
		if (session == null) {
			throw new IllegalArgumentException("Session not found: " + sessionId);
		}
		return session;
	}
}
