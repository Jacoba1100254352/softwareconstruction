package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightPiece implements ChessPiece, Cloneable {
    private final ChessGame.TeamColor teamColor;
    private boolean hasMoved = false;

    public KnightPiece(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public ChessGame.TeamColor teamColor() {
        return teamColor;
    }

    @Override
    public KnightPiece clone() {
        try {
            return (KnightPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();  // Should never happen
        }
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

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }


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

            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8)
                continue; // Move is outside the bounds of the board

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
