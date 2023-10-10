package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingPiece implements ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private boolean hasMoved = false;

    public KingPiece(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
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
    public PieceType getPieceType() {
        // Return the type of this piece: King
        return PieceType.KING;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Add standard moves for the king
        addStandardMoves(board, myPosition, moves);

        // Add special moves for the king: castling
        addCastlingMoves(board, myPosition, moves);

        return moves;
    }

    /**
     * Adds the standard moves for the king (one step in all directions)
     * to the given moves collection.
     */
    private void addStandardMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
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
    }

    /**
     * Adds the castling moves for the king (if valid) to the given moves collection.
     */
    private void addCastlingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {

        // Check and add king-side castling move
        if (canCastleKingSide(board, myPosition))
            moves.add(new ChessMoveImpl(myPosition, new ChessPositionImpl(myPosition.row(), myPosition.column() + 2), null));

        // Check and add queen-side castling move
        if (canCastleQueenSide(board, myPosition))
            moves.add(new ChessMoveImpl(myPosition, new ChessPositionImpl(myPosition.row(), myPosition.column() - 2), null));
    }

    public Collection<ChessMove> basicPieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Add standard moves for the king
        addStandardMoves(board, myPosition, moves);

        return moves;
    }

    /**
     * Checks if a given square on the board is under attack by opponent's pieces.
     */
    private boolean isSquareThreatened(ChessBoard board, ChessPosition position) {
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);

            if (piece == null)
                return false;

            Collection<ChessMove> moves = (piece instanceof KingPiece) ?
                 ((KingPiece) piece).basicPieceMoves(board, pos) :
                    piece.pieceMoves(board, pos);


            for (ChessMove move : moves)
                if (move.getEndPosition().equals(position))
                    return true; // Found an attacking move on the position
        }
        return false; // No attacks found on the position
    }

    private boolean canCastleKingSide(ChessBoard board, ChessPosition position) {
        if (position.column() >= 7 || hasMoved) return false;

        // Check if squares between the king and rook are free
        for (int col = position.column() + 1; col <= 7; col++)
            if (board.getPiece(new ChessPositionImpl(position.row(), col)) != null)
                return false;

        // Check if the squares between the king and rook are not under attack
        for (int col = position.column(); col <= 7; col++)
            if (isSquareThreatened(board, new ChessPositionImpl(position.row(), col)))
                return false;

        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.row(), 8));
        return rook != null && rook.getPieceType() == PieceType.ROOK && !rook.hasMoved();
    }

    private boolean canCastleQueenSide(ChessBoard board, ChessPosition position) {
        if (position.column() <= 2 || hasMoved) return false;

        // Check if squares between the king and rook are free
        for (int col = position.column() - 1; col >= 2; col--)
            if (board.getPiece(new ChessPositionImpl(position.row(), col)) != null)
                return false;

        // Check if the squares between the king and rook are not under attack
        for (int col = position.column(); col >= 2; col--)
            if (isSquareThreatened(board, new ChessPositionImpl(position.row(), col)))
                return false;

        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.row(), 1));
        return rook != null && rook.getPieceType() == PieceType.ROOK && !rook.hasMoved();
    }
}
