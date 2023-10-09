package chess;

public class ChessMoveImpl implements ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMoveImpl(ChessPosition start, ChessPosition end, ChessPiece.PieceType promotion, ChessBoard board) {
        this.startPosition = start;
        this.endPosition = end;
        this.promotionPiece = promotion;
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

    @Override
    public String toString() {
        return "Move[" + startPosition + " -> " + endPosition + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessMoveImpl that = (ChessMoveImpl) o;

        if (!startPosition.equals(that.startPosition)) return false;
        if (!endPosition.equals(that.endPosition)) return false;
        return promotionPiece == that.promotionPiece;
    }

    @Override
    public int hashCode() {
        int result = startPosition.hashCode();
        result = 31 * result + endPosition.hashCode();
        result = 31 * result + (promotionPiece != null ? promotionPiece.hashCode() : 0);
        return result;
    }
}
