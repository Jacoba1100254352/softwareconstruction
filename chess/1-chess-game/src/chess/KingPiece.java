package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingPiece implements ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private boolean hasMoved;

    public KingPiece(ChessGame.TeamColor teamColor) {
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
        int rowDiff = Math.abs(to.row() - from.row());
        int colDiff = Math.abs(to.column() - from.column());

        return (rowDiff <= 1 && colDiff <= 1) && (rowDiff != 0 || colDiff != 0);
    }


    @Override
    public PieceType getPieceType() {
        return PieceType.KING;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Add standard moves for the king
        // Possible directions for the king to move
        int[][] directions = {
                {1, 0}, {1, 1}, {0, 1}, {-1, 1},
                {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
        };

        // Iterate through all possible moves and validate
        for (int[] direction : directions) {
            int newRow = myPosition.row() + direction[0];
            int newCol = myPosition.column() + direction[1];

            // Skip if the move is outside the board
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) continue;

            ChessPosition newPosition = new ChessPositionImpl(newRow, newCol);
            ChessPiece targetPiece = board.getPiece(newPosition);

            // Add move if target square is empty or contains opponent's piece
            if (targetPiece == null || targetPiece.teamColor() != this.teamColor()) {
                moves.add(new ChessMoveImpl(myPosition, newPosition, null));
            }
        }

        return moves;
    }
}
