package blindchess.bot;


import blindchess.notation.ChessNotation;
import blindchess.notation.ChessRules;
import chess.gameplay.ChessGame;
import chess.gameplay.ChessGameCopier;
import chess.gameplay.ChessGameImpl;
import chess.gameplay.ChessMove;
import chess.gameplay.ChessPosition;
import chess.gameplay.ChessPositionImpl;
import chess.gameplay.InvalidMoveException;
import chess.pieces.ChessPiece;

import java.util.Comparator;
import java.util.List;


public class MinimaxChessBot implements ChessBot
{
	private static final double CHECKMATE_SCORE = 100_000;
	private final int searchDepth;

	public MinimaxChessBot() {
		this(2);
	}

	public MinimaxChessBot(int searchDepth) {
		this.searchDepth = Math.max(1, searchDepth);
	}

	@Override
	public ChessMove chooseMove(ChessGame game, ChessGame.TeamColor teamColor) {
		List<ChessMove> legalMoves = ChessRules.legalMoves(game, teamColor);
		if (legalMoves.isEmpty()) {
			throw new IllegalStateException("No legal moves available for the bot.");
		}

		legalMoves.sort(Comparator.comparing(ChessNotation::toCoordinateString));

		double bestScore = Double.NEGATIVE_INFINITY;
		ChessMove bestMove = legalMoves.get(0);
		for (ChessMove move : legalMoves) {
			ChessGameImpl simulation = ChessGameCopier.copy(game);
			simulation.setTeamTurn(teamColor);
			try {
				simulation.makeMove(ChessGameCopier.copyMove(move));
			} catch (InvalidMoveException e) {
				continue;
			}

			double score = minimax(simulation, searchDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, teamColor);
			if (score > bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		return bestMove;
	}

	private double minimax(ChessGame game, int depth, double alpha, double beta, ChessGame.TeamColor botColor) {
		ChessGame.TeamColor currentTurn = game.getTeamTurn();
		if (currentTurn == null) {
			return evaluate(game, botColor);
		}
		if (game.isInCheckmate(currentTurn)) {
			return currentTurn == botColor ? -CHECKMATE_SCORE - depth : CHECKMATE_SCORE + depth;
		}
		if (game.isInStalemate(currentTurn) || depth == 0) {
			return evaluate(game, botColor);
		}

		List<ChessMove> legalMoves = ChessRules.legalMoves(game, currentTurn);
		legalMoves.sort(Comparator.comparing(ChessNotation::toCoordinateString));
		if (legalMoves.isEmpty()) {
			return evaluate(game, botColor);
		}

		if (currentTurn == botColor) {
			double best = Double.NEGATIVE_INFINITY;
			for (ChessMove move : legalMoves) {
				best = Math.max(best, evaluateChild(game, currentTurn, move, depth, alpha, beta, botColor));
				alpha = Math.max(alpha, best);
				if (beta <= alpha) {
					break;
				}
			}
			return best;
		}

		double best = Double.POSITIVE_INFINITY;
		for (ChessMove move : legalMoves) {
			best = Math.min(best, evaluateChild(game, currentTurn, move, depth, alpha, beta, botColor));
			beta = Math.min(beta, best);
			if (beta <= alpha) {
				break;
			}
		}
		return best;
	}

	private double evaluateChild(
			ChessGame game,
			ChessGame.TeamColor movingTeam,
			ChessMove move,
			int depth,
			double alpha,
			double beta,
			ChessGame.TeamColor botColor
	) {
		ChessGameImpl simulation = ChessGameCopier.copy(game);
		simulation.setTeamTurn(movingTeam);
		try {
			simulation.makeMove(ChessGameCopier.copyMove(move));
		} catch (InvalidMoveException e) {
			return movingTeam == botColor ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		}
		return minimax(simulation, depth - 1, alpha, beta, botColor);
	}

	private double evaluate(ChessGame game, ChessGame.TeamColor botColor) {
		ChessGame.TeamColor opposingColor = opposite(botColor);
		double material = materialScore(game, botColor) - materialScore(game, opposingColor);
		double mobility = ChessRules.legalMoves(game, botColor).size() - ChessRules.legalMoves(game, opposingColor).size();
		double checkPressure = 0;
		if (game.getTeamTurn() != null) {
			if (game.isInCheck(opposingColor)) {
				checkPressure += 30;
			}
			if (game.isInCheck(botColor)) {
				checkPressure -= 30;
			}
		}
		return material + (mobility * 3) + checkPressure;
	}

	private int materialScore(ChessGame game, ChessGame.TeamColor teamColor) {
		int score = 0;
		for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
			ChessPiece piece = game.getBoard().getPiece(position);
			if (piece != null && piece.teamColor() == teamColor) {
				score += pieceValue(piece.getPieceType());
			}
		}
		return score;
	}

	private int pieceValue(ChessPiece.PieceType pieceType) {
		return switch (pieceType) {
			case PAWN -> 100;
			case KNIGHT -> 320;
			case BISHOP -> 330;
			case ROOK -> 500;
			case QUEEN -> 900;
			case KING -> 20_000;
		};
	}

	private ChessGame.TeamColor opposite(ChessGame.TeamColor color) {
		return color == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
	}
}
