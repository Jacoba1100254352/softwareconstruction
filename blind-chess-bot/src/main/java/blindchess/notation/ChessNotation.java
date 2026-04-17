package blindchess.notation;


import chess.gameplay.ChessGame;
import chess.gameplay.ChessGameCopier;
import chess.gameplay.ChessGameImpl;
import chess.gameplay.ChessMove;
import chess.gameplay.InvalidMoveException;
import chess.pieces.ChessPiece;

import java.util.List;


public final class ChessNotation
{
	private ChessNotation() {
	}

	public static ChessMove parse(String rawNotation, ChessGame game) {
		if (game.getTeamTurn() == null) {
			throw new IllegalStateException("Game is already over.");
		}

		String normalizedInput = normalizeNotation(rawNotation);
		if (normalizedInput.isBlank()) {
			throw new IllegalArgumentException("Move notation cannot be empty.");
		}

		List<ChessMove> legalMoves = ChessRules.legalMoves(game, game.getTeamTurn());
		for (ChessMove move : legalMoves) {
			if (normalizeNotation(toSan(game, move)).equals(normalizedInput)) {
				return move;
			}
		}

		for (ChessMove move : legalMoves) {
			String coordinate = normalizeNotation(toCoordinateString(move));
			if (coordinate.equals(normalizedInput) || coordinate.replace("=", "").equals(normalizedInput)) {
				return move;
			}
		}

		throw new IllegalArgumentException("Illegal or unrecognized move notation: " + rawNotation);
	}

	public static String toSan(ChessGame game, ChessMove move) {
		ChessPiece movingPiece = game.getBoard().getPiece(move.getStartPosition());
		if (movingPiece == null) {
			throw new IllegalArgumentException("No piece found at the starting position.");
		}

		if (movingPiece.getPieceType() == ChessPiece.PieceType.KING) {
			int fileDelta = move.getEndPosition().getCol() - move.getStartPosition().getCol();
			if (Math.abs(fileDelta) == 2) {
				String castle = (fileDelta > 0) ? "O-O" : "O-O-O";
				return castle + checkSuffix(game, move, movingPiece.teamColor());
			}
		}

		boolean capture = ChessRules.isCapture(game, move);
		StringBuilder san = new StringBuilder();

		if (movingPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
			san.append(pieceLetter(movingPiece.getPieceType()));
			san.append(disambiguation(game, move, movingPiece));
		} else if (capture) {
			san.append(fileChar(move.getStartPosition().getCol()));
		}

		if (capture) {
			san.append('x');
		}

		san.append(squareName(move.getEndPosition().getCol(), move.getEndPosition().getRow()));
		if (move.getPromotionPiece() != null) {
			san.append('=').append(pieceLetter(move.getPromotionPiece()));
		}

		san.append(checkSuffix(game, move, movingPiece.teamColor()));
		return san.toString();
	}

	public static String toCoordinateString(ChessMove move) {
		StringBuilder coordinate = new StringBuilder();
		coordinate.append(squareName(move.getStartPosition().getCol(), move.getStartPosition().getRow()));
		coordinate.append(squareName(move.getEndPosition().getCol(), move.getEndPosition().getRow()));
		if (move.getPromotionPiece() != null) {
			coordinate.append('=').append(pieceLetter(move.getPromotionPiece()));
		}
		return coordinate.toString();
	}

	public static String normalizeNotation(String notation) {
		return notation == null ? "" : notation
				.trim()
				.replace('0', 'O')
				.replaceAll("\\s+", "")
				.replaceAll("[+#?!]+$", "")
				.replace("-", "")
				.toUpperCase();
	}

	private static String disambiguation(ChessGame game, ChessMove move, ChessPiece movingPiece) {
		List<ChessMove> legalMoves = ChessRules.legalMoves(game, movingPiece.teamColor());
		List<ChessMove> conflicts = legalMoves.stream()
				.filter(candidate -> !candidate.getStartPosition().equals(move.getStartPosition()))
				.filter(candidate -> candidate.getEndPosition().equals(move.getEndPosition()))
				.filter(candidate -> {
					ChessPiece candidatePiece = game.getBoard().getPiece(candidate.getStartPosition());
					return candidatePiece != null && candidatePiece.getPieceType() == movingPiece.getPieceType();
				})
				.toList();

		if (conflicts.isEmpty()) {
			return "";
		}

		boolean sameFile = conflicts.stream()
				.anyMatch(candidate -> candidate.getStartPosition().getCol() == move.getStartPosition().getCol());
		boolean sameRank = conflicts.stream()
				.anyMatch(candidate -> candidate.getStartPosition().getRow() == move.getStartPosition().getRow());

		if (!sameFile) {
			return String.valueOf(fileChar(move.getStartPosition().getCol()));
		}
		if (!sameRank) {
			return String.valueOf(move.getStartPosition().getRow());
		}
		return new StringBuilder()
				.append(fileChar(move.getStartPosition().getCol()))
				.append(move.getStartPosition().getRow())
				.toString();
	}

	private static String checkSuffix(ChessGame game, ChessMove move, ChessGame.TeamColor movingTeam) {
		ChessGameImpl simulation = ChessGameCopier.copy(game);
		simulation.setTeamTurn(movingTeam);
		try {
			simulation.makeMove(ChessGameCopier.copyMove(move));
		} catch (InvalidMoveException e) {
			throw new IllegalStateException("Unable to render SAN for move.", e);
		}

		ChessGame.TeamColor opposingTeam = (movingTeam == ChessGame.TeamColor.WHITE)
				? ChessGame.TeamColor.BLACK
				: ChessGame.TeamColor.WHITE;

		if (simulation.isInCheckmate(opposingTeam)) {
			return "#";
		}
		if (simulation.isInCheck(opposingTeam)) {
			return "+";
		}
		return "";
	}

	private static char pieceLetter(ChessPiece.PieceType pieceType) {
		return switch (pieceType) {
			case KING -> 'K';
			case QUEEN -> 'Q';
			case ROOK -> 'R';
			case BISHOP -> 'B';
			case KNIGHT -> 'N';
			case PAWN -> 'P';
		};
	}

	private static char fileChar(int column) {
		return (char) ('a' + column - 1);
	}

	private static String squareName(int column, int row) {
		return "" + fileChar(column) + row;
	}
}
