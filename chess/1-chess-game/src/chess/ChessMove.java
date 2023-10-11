package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this interface, but you should not alter the existing
 * methods.
 */
public interface ChessMove {
    /**
     * @return ChessPosition of starting location
     */
    ChessPosition getStartPosition();

    /**
     * @return ChessPosition of ending location
     */
    ChessPosition getEndPosition();

    /**
     * @return Represented piece to promote to or promoted to
     */
    ChessPiece.PieceType getPromotionPiece();
}
