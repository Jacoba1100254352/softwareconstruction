package chess;

public record ChessMoveImpl(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) implements ChessMove {

    @Override
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    @Override
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public boolean doesMoveResultInCheck(ChessPosition start, ChessPiece piece, ChessBoard board) {
        // 1. Deep clone the board
        ChessBoard clonedBoard = board.clone();

        // 2. Apply the move on the cloned board
        ChessPosition endPosition = this.getEndPosition();
        clonedBoard.addPiece(endPosition, piece); // move the piece to the end position
        clonedBoard.removePiece(start); // remove the piece from the start position

        // 3. Check if the king of the piece's color is under threat
        ChessPosition kingPosition = clonedBoard.getKingPosition(piece.teamColor());
        return clonedBoard.isSquareUnderThreat(kingPosition, piece.teamColor());
    }
}
