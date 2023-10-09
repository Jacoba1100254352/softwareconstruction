package chess;

import java.util.ArrayList;
import java.util.Collection;

public record QueenPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Directions: North, East, South, West, Northeast, Northwest, Southeast, Southwest
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int row = myPosition.row();
            int col = myPosition.column();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (row < 1 || row > 8 || col < 1 || col > 8)
                    break; // Out of board bounds

                ChessPosition newPosition = new ChessPositionImpl(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    // Empty square, add as a possible move
                    moves.add(new ChessMoveImpl(myPosition, newPosition, null));
                } else if (pieceAtNewPosition.teamColor() != this.teamColor()) {
                    // Opponent's piece, capture it and break
                    moves.add(new ChessMoveImpl(myPosition, newPosition, null));
                    break;
                } else break; // Own piece, block the path

            }
        }

        return moves;
    }
}
