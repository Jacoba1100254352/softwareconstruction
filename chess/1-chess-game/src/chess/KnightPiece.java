package chess;

import java.util.ArrayList;
import java.util.Collection;

public record KnightPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.KNIGHT;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Potential moves for a knight, representing the "L" shape movement
        int[][] directions = {
                {2, 1}, {1, 2}, {-2, 1}, {-1, 2},
                {2, -1}, {1, -2}, {-2, -1}, {-1, -2}
        };

        for (int[] direction : directions) {
            int newRow = myPosition.row() + direction[0];
            int newCol = myPosition.column() + direction[1];

            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                // Move is outside the bounds of the board
                continue;
            }

            ChessPosition newPosition = new ChessPositionImpl(newRow, newCol);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            if (pieceAtNewPosition == null || pieceAtNewPosition.teamColor() != this.teamColor()) {
                // Either the square is empty, or there's an opponent's piece that can be captured
                moves.add(new ChessMoveImpl(myPosition, newPosition, null));
            }
        }

        return moves;
    }
}
