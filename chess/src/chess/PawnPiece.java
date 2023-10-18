package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnPiece implements ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private boolean hasMoved = false;

    // Constructor initializes the pawn's color
    public PawnPiece(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    // Getter for the pawn's team color
    @Override
    public ChessGame.TeamColor teamColor() {
        return teamColor;
    }

    // Check if the pawn has moved before
    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    // Mark the pawn as having moved
    @Override
    public void markAsMoved() {
        hasMoved = true;
    }

    // Return the pawn's piece type
    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    // Compute all valid moves for the pawn from its current position
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int newRow = myPosition.row() + direction;

        if (newRow >= 1 && newRow <= 8) {
            checkForwardMoves(board, myPosition, newRow, moves, direction);
            checkDiagonalCaptures(board, myPosition, newRow, moves);
        }

        return moves;
    }

    // Check and add valid forward moves for the pawn
    private void checkForwardMoves(ChessBoard board, ChessPosition myPosition, int newRow, Collection<ChessMove> moves, int direction) {
        ChessPosition forwardOne = new ChessPositionImpl(newRow, myPosition.column());

        // If the forward square is empty
        if (board.getPiece(forwardOne) == null) {
            addMoveWithPromotion(myPosition, forwardOne, moves);

            // Check for double move from starting position
            if (isNewPawnPosition(myPosition) && board.getPiece(new ChessPositionImpl(myPosition.row() + (2 * direction), myPosition.column())) == null) {
                moves.add(new ChessMoveImpl(myPosition, new ChessPositionImpl(myPosition.row() + (2 * direction), myPosition.column()), null));
            }
        }
    }

    // Check if the pawn is on its initial row
    private boolean isNewPawnPosition(ChessPosition position) {
        return (teamColor == ChessGame.TeamColor.WHITE && position.row() == 2) || (teamColor == ChessGame.TeamColor.BLACK && position.row() == 7);
    }

    // Check and add valid diagonal capture moves for the pawn
    private void checkDiagonalCaptures(ChessBoard board, ChessPosition myPosition, int newRow, Collection<ChessMove> moves) {
        for (int diagDirection : new int[]{-1, 1}) {
            int newCol = myPosition.column() + diagDirection;

            // Check if the new column is within valid bounds (1 to 8)
            if (newCol < 1 || newCol > 8)
                continue;

            ChessPosition diagonal = new ChessPositionImpl(newRow, newCol);

            // If there's an opponent piece on the diagonal
            ChessPiece pieceAtDiagonal = board.getPiece(diagonal);
            if (pieceAtDiagonal != null && pieceAtDiagonal.teamColor() != teamColor)
                addMoveWithPromotion(myPosition, diagonal, moves);
        }
    }

    // Check for pawn promotion and add valid moves accordingly
    private void addMoveWithPromotion(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        if (end.row() == 1 || end.row() == 8)
            // Add promotion moves for the pawn
            for (PieceType type : new PieceType[]{PieceType.QUEEN, PieceType.BISHOP, PieceType.ROOK, PieceType.KNIGHT})
                moves.add(new ChessMoveImpl(start, end, type));
        else moves.add(new ChessMoveImpl(start, end, null));
    }
}
