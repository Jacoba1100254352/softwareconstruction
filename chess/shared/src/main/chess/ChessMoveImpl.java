package chess;

public record ChessMoveImpl(ChessPosition startPosition, ChessPosition endPosition,
                            ChessPiece.PieceType promotionPiece) implements ChessMove {

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