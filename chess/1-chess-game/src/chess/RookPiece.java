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

        // Directions: North, East, South, West
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

        for (int[] direction : directions) {
            int row = myPosition.row();
            int col = myPosition.column();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    // Out of board bounds
                    break;
                }

                ChessPosition newPosition = new ChessPositionImpl(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    // Empty square, add as a possible move
                    moves.add(new ChessMoveImpl(myPosition, newPosition, null, board));
                } else if (pieceAtNewPosition.teamColor() != this.teamColor()) {
                    // Opponent's piece, capture it and break
                    moves.add(new ChessMoveImpl(myPosition, newPosition, null, board));
                    break;
                } else {
                    // Own piece, block the path
                    break;
                }
            }
        }

        return moves;
    }
}
