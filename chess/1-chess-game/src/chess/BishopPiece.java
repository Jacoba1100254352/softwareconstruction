package chess;

import java.util.ArrayList;
import java.util.Collection;

public record BishopPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.BISHOP;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Bishops move diagonally.
        int[][] directions = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

        for (int[] dir : directions) {
            for (int i = 1; i <= 8; i++) {
                int newRow = myPosition.row() + dir[0] * i;
                int newCol = myPosition.column() + dir[1] * i;

                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8)
                    break;  // Outside of board bounds

                ChessPosition newPos = new ChessPositionImpl(newRow, newCol);

                ChessPiece pieceAtNewPos = board.getPiece(newPos);
                if (pieceAtNewPos != null) {
                    if (pieceAtNewPos.teamColor() != this.teamColor)
                        moves.add(new ChessMoveImpl(myPosition, newPos, null, board));
                    break;
                } else moves.add(new ChessMoveImpl(myPosition, newPos, null, board));
            }
        }
        return moves;
    }
}
