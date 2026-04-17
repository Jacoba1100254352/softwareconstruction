package chess.gameplay;


import chess.pieces.ChessPiece;
import chess.pieces.ChessPieceImpl;


public final class ChessGameCopier
{
	private ChessGameCopier() {
	}

	public static ChessGameImpl copy(ChessGame source) {
		ChessGameImpl copy = new ChessGameImpl();
		copy.setTeamTurn(source.getTeamTurn());

		ChessBoardImpl copiedBoard = new ChessBoardImpl();
		copiedBoard.setTestingMode(source.getBoard().getTestingMode());

		for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
			ChessPiece piece = source.getBoard().getPiece(position);
			if (piece == null) {
				continue;
			}

			ChessPiece copiedPiece = new ChessPieceImpl(piece.teamColor(), piece.getPieceType());
			if (piece.hasMoved()) {
				copiedPiece.markAsMoved();
			}
			copiedBoard.addPiece(new ChessPositionImpl(position.getRow(), position.getCol()), copiedPiece);
		}

		ChessMove lastMove = source.getBoard().getLastMove();
		if (lastMove != null) {
			copiedBoard.setLastMove(copyMove(lastMove));
		}

		copy.setBoard(copiedBoard);
		return copy;
	}

	public static ChessMove copyMove(ChessMove move) {
		return new ChessMoveImpl(
				new ChessPositionImpl(move.getStartPosition().getRow(), move.getStartPosition().getCol()),
				new ChessPositionImpl(move.getEndPosition().getRow(), move.getEndPosition().getCol()),
				move.getPromotionPiece()
		);
	}
}
