import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;


final class PassoffChessAdapter
{
	private PassoffChessAdapter() {
	}
	
	static ChessMove move(chess.gameplay.ChessMove move) {
		return new MoveAdapter(move);
	}
	
	private record MoveAdapter(
			ChessPosition startPosition,
			ChessPosition endPosition,
			ChessPiece.PieceType promotionPiece
	) implements ChessMove
	{
		private MoveAdapter(chess.gameplay.ChessMove move) {
			this(position(move.getStartPosition()), position(move.getEndPosition()), pieceType(move.getPromotionPiece()));
		}
		
		@Override
		public ChessPosition getStartPosition() {
			return startPosition;
		}
		
		@Override
		public ChessPosition getEndPosition() {
			return endPosition;
		}
		
		@Override
		public ChessPiece.PieceType getPromotionPiece() {
			return promotionPiece;
		}
	}
	
	private record PositionAdapter(int row, int column) implements ChessPosition
	{
		@Override
		public int getRow() {
			return row;
		}
		
		@Override
		public int getColumn() {
			return column;
		}
	}
	
	private static ChessPosition position(chess.gameplay.ChessPosition position) {
		return new PositionAdapter(position.getRow(), position.getCol());
	}
	
	private static ChessPiece.PieceType pieceType(chess.pieces.ChessPiece.PieceType pieceType) {
		return pieceType == null ? null : ChessPiece.PieceType.valueOf(pieceType.name());
	}
}
