package blindchess.notation;


import chess.gameplay.ChessGame;
import chess.gameplay.ChessGameCopier;
import chess.gameplay.ChessGameImpl;
import chess.gameplay.ChessMove;
import chess.gameplay.ChessPosition;
import chess.gameplay.ChessPositionImpl;
import chess.pieces.ChessPiece;

import java.util.ArrayList;
import java.util.List;


public final class ChessRules
{
	private ChessRules() {
	}

	public static List<ChessMove> legalMoves(ChessGame game, ChessGame.TeamColor teamColor) {
		ChessGameImpl simulation = ChessGameCopier.copy(game);
		simulation.setTeamTurn(teamColor);

		List<ChessMove> legalMoves = new ArrayList<>();
		for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
			ChessPiece piece = simulation.getBoard().getPiece(position);
			if (piece != null && piece.teamColor() == teamColor) {
				legalMoves.addAll(simulation.validMoves(position));
			}
		}
		return legalMoves;
	}

	public static boolean isCapture(ChessGame game, ChessMove move) {
		ChessPiece movingPiece = game.getBoard().getPiece(move.getStartPosition());
		if (game.getBoard().getPiece(move.getEndPosition()) != null) {
			return true;
		}
		return movingPiece != null &&
				movingPiece.getPieceType() == ChessPiece.PieceType.PAWN &&
				move.getStartPosition().getCol() != move.getEndPosition().getCol();
	}
}
