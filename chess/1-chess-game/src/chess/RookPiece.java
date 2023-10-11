package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookPiece implements ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private boolean hasMoved;

    public RookPiece(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
        this.hasMoved = false;
    }

    public ChessGame.TeamColor teamColor() {
        return teamColor;
    }

    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public void markAsMoved() {
        hasMoved = true;
    }

    @Override
    public boolean canAttack(ChessBoard board, ChessPosition from, ChessPosition to) {
        if (from.row() == to.row()) {
            int start = Math.min(from.column(), to.column()) + 1;
            int end = Math.max(from.column(), to.column());
            for (int col = start; col < end; col++) {
                if (board.getPiece(new ChessPositionImpl(from.row(), col)) != null) return false;
            }
            return true;
        } else if (from.column() == to.column()) {
            int start = Math.min(from.row(), to.row()) + 1;
            int end = Math.max(from.row(), to.row());
            for (int row = start; row < end; row++) {
                if (board.getPiece(new ChessPositionImpl(row, from.column())) != null) return false;
            }
            return true;
        }
        return false;
    }


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
