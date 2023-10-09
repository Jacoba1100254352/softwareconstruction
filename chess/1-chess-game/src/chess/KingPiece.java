package chess;

import java.util.ArrayList;
import java.util.Collection;

public record KingPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.KING;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Kings can move one square in any direction
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };

        for (int[] dir : directions) {
            int newRow = myPosition.row() + dir[0];
            int newCol = myPosition.column() + dir[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPositionImpl(newRow, newCol);
                ChessPiece pieceAtNewPos = board.getPiece(newPos);

                if (pieceAtNewPos == null || pieceAtNewPos.teamColor() != this.teamColor) {
                    moves.add(new ChessMoveImpl(myPosition, newPos, null, board));
                }
            }
        }
        return moves;
    }
}
