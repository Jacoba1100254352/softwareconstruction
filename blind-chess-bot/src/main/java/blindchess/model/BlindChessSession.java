package blindchess.model;


import blindchess.bot.ChessBot;
import blindchess.notation.ChessNotation;
import chess.gameplay.ChessGame;
import chess.gameplay.ChessGameImpl;
import chess.gameplay.ChessMove;
import chess.gameplay.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;


public class BlindChessSession
{
	private final String sessionId;
	private final GameMode mode;
	private final ChessGame.TeamColor playerColor;
	private final ChessGame.TeamColor botColor;
	private final ChessBot bot;
	private final ChessGameImpl game;
	private final List<String> moveHistory = new ArrayList<>();
	private SessionStatus status = SessionStatus.ACTIVE;
	private String resultMessage;

	public BlindChessSession(String sessionId, GameMode mode, ChessGame.TeamColor playerColor, ChessBot bot) {
		this.sessionId = sessionId;
		this.mode = mode;
		this.playerColor = playerColor;
		this.botColor = playerColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
		this.bot = bot;
		this.game = new ChessGameImpl();
		this.game.getBoard().resetBoard();

		if (playerColor == ChessGame.TeamColor.BLACK) {
			runBotTurn();
		}
	}

	public synchronized TurnOutcome playPlayerMove(String notation) {
		requireActiveGame();
		if (game.getTeamTurn() != playerColor) {
			throw new IllegalStateException("It is not your turn.");
		}

		ChessMove playerMove = ChessNotation.parse(notation, game);
		String playerSan = ChessNotation.toSan(game, playerMove);
		try {
			game.makeMove(playerMove);
		} catch (InvalidMoveException e) {
			throw new IllegalArgumentException("Invalid move: " + notation, e);
		}
		moveHistory.add(playerSan);
		updateStatusAfterMove(playerColor);

		String botSan = null;
		if (status == SessionStatus.ACTIVE && game.getTeamTurn() == botColor) {
			botSan = runBotTurn();
		}

		return new TurnOutcome(playerSan, botSan);
	}

	public synchronized void resignPlayer() {
		if (status != SessionStatus.ACTIVE) {
			return;
		}
		status = SessionStatus.RESIGNED;
		resultMessage = "You resigned. Bot wins.";
		game.setTeamTurn(null);
	}

	public synchronized String getSessionId() {
		return sessionId;
	}

	public synchronized GameMode getMode() {
		return mode;
	}

	public synchronized ChessGame.TeamColor getPlayerColor() {
		return playerColor;
	}

	public synchronized ChessGame.TeamColor getBotColor() {
		return botColor;
	}

	public synchronized ChessGame.TeamColor getCurrentTurn() {
		return game.getTeamTurn();
	}

	public synchronized SessionStatus getStatus() {
		return status;
	}

	public synchronized String getResultMessage() {
		return resultMessage;
	}

	public synchronized String getLatestMove() {
		return moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size() - 1);
	}

	public synchronized List<String> getVisibleHistory() {
		if (moveHistory.isEmpty()) {
			return List.of();
		}
		if (mode == GameMode.NO_HISTORY) {
			return List.of(moveHistory.get(moveHistory.size() - 1));
		}
		return formatMovePairs();
	}

	public synchronized boolean isPlayerTurn() {
		return status == SessionStatus.ACTIVE && game.getTeamTurn() == playerColor;
	}

	public synchronized String getPrompt() {
		if (status != SessionStatus.ACTIVE) {
			return resultMessage;
		}
		if (game.getTeamTurn() == playerColor) {
			return game.isInCheck(playerColor) ? "Your king is in check." : "Your move.";
		}
		return game.isInCheck(botColor) ? "Bot is in check." : "Bot to move.";
	}

	private String runBotTurn() {
		ChessMove botMove = bot.chooseMove(game, botColor);
		String botSan = ChessNotation.toSan(game, botMove);
		try {
			game.makeMove(botMove);
		} catch (InvalidMoveException e) {
			throw new IllegalStateException("Bot produced an invalid move.", e);
		}
		moveHistory.add(botSan);
		updateStatusAfterMove(botColor);
		return botSan;
	}

	private void updateStatusAfterMove(ChessGame.TeamColor movingTeam) {
		ChessGame.TeamColor defendingTeam = game.getTeamTurn();
		if (defendingTeam == null) {
			return;
		}
		if (game.isInCheckmate(defendingTeam)) {
			status = SessionStatus.CHECKMATE;
			resultMessage = movingTeam == playerColor ? "Checkmate. You win." : "Checkmate. Bot wins.";
			game.setTeamTurn(null);
			return;
		}
		if (game.isInStalemate(defendingTeam)) {
			status = SessionStatus.STALEMATE;
			resultMessage = "Stalemate.";
			game.setTeamTurn(null);
		}
	}

	private List<String> formatMovePairs() {
		List<String> lines = new ArrayList<>();
		for (int index = 0; index < moveHistory.size(); index += 2) {
			StringBuilder line = new StringBuilder();
			line.append((index / 2) + 1).append(". ").append(moveHistory.get(index));
			if (index + 1 < moveHistory.size()) {
				line.append(' ').append(moveHistory.get(index + 1));
			}
			lines.add(line.toString());
		}
		return List.copyOf(lines);
	}

	private void requireActiveGame() {
		if (status != SessionStatus.ACTIVE) {
			throw new IllegalStateException(resultMessage == null ? "Game is over." : resultMessage);
		}
	}

	public record TurnOutcome(String playerMove, String botMove) {
	}
}
