package chess;

import java.util.ArrayList;
import java.util.Collection;

public record RookPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.ROOK;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Rook can move horizontally or vertically.
        // Check each direction until a piece is encountered or the edge of the board is reached.
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            for (int i = 1; i <= 8; i++) {
                int newRow = myPosition.row() + dir[0] * i;
                int newCol = myPosition.column() + dir[1] * i;

                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                    break;  // Outside of board bounds
                }

                ChessPosition newPos = new ChessPositionImpl(newRow, newCol);
                ChessPiece pieceAtNewPos = board.getPiece(newPos);
                if (pieceAtNewPos != null) {
                    if (pieceAtNewPos.teamColor() != this.teamColor) {
                        moves.add(new ChessMoveImpl(myPosition, newPos, null, board));
                    }
                    break;  // Rook's path is blocked
                } else {
                    moves.add(new ChessMoveImpl(myPosition, newPos, null, board));
                }
            }
        }

        return moves;
    }
}
