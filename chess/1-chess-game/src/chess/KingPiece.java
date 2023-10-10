package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

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

    /**
     * Return all valid moves for the king excluding castling moves.
     */
    public Collection<ChessMove> nonCastlingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Add standard moves for the king
        addStandardMoves(board, myPosition, moves);

        return moves;
    }

    /**
     * Checks if a given square on the board is under attack by opponent's pieces.
     */
    private boolean isSquareUnderAttack(ChessBoard board, ChessPosition position) {
        // Determine the enemy's color
        ChessGame.TeamColor enemyColor = (teamColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        // Iterate through all board positions to find attacking pieces
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece != null && piece.teamColor() == enemyColor) {
                Collection<ChessMove> moves;
                moves = (piece instanceof KingPiece) ? ((KingPiece) piece).nonCastlingMoves(board, pos) :
                                                        piece.pieceMoves(board, pos);

                if (moves.stream().anyMatch(move -> move.getEndPosition().equals(position)))
                    return true;  // Found an attacking move on the position
            }
        }
        return false;  // No attacks found on the position
    }

    // In KingPiece class
    private boolean rookHasMoved(ChessBoard board, ChessPosition kingPosition, boolean kingSide) {
        ChessPosition rookPosition = kingSide ?
                new ChessPositionImpl(kingPosition.row(), 8) :  // King-side rook
                new ChessPositionImpl(kingPosition.row(), 1);  // Queen-side rook

        ChessPiece rook = board.getPiece(rookPosition);
        return rook == null || rook.getPieceType() != PieceType.ROOK || rook.hasMoved();
    }

    /**
     * Checks if the king has moved from its original starting position.
     */
    private boolean hasMoved(ChessPosition position) {
        return (teamColor == ChessGame.TeamColor.WHITE && (position.row() != 1 || position.column() != 5))
                || (teamColor == ChessGame.TeamColor.BLACK && (position.row() != 8 || position.column() != 5));
    }

    /**
     * Checks if king-side castling is possible from a given position.
     */
    private boolean canCastleKingSide(ChessBoard board, ChessPosition position) {
        System.out.println("Start k");
        for (ChessPosition position2 : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position2);
            if (piece != null)// Do something with the piece
                System.out.println("Position: " + position2 + ", Piece: " + piece + ", Piece type = " + piece.getPieceType() + ", Piece has moved = " + piece.hasMoved());
        }
        System.out.println("End k");

        // Preliminary check: If king is close to the edge, castling is not possible
        if (position.column() >= 7 || hasMoved(position)) return false;

        // Check if the path for castling is clear and safe
        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.row(), 8));
        if (rook == null || rook.getPieceType() != PieceType.ROOK) return false;

        System.out.println("K RookHasMoved = " + rookHasMoved(board, position, true) + ", rook.hasMoved() = " + rook.hasMoved() + ", Rook = " + rook);

        // Check if rook has moved
        if (rookHasMoved(board, position, true)) return false;

        for (int i = 1; i <= 2; i++)
            if (board.getPiece(new ChessPositionImpl(position.row(), position.column() + i)) != null || isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() + i)))
                return false;
        return true;
    }



    /**
     * Checks if queen-side castling is possible from a given position.
     */
    private boolean canCastleQueenSide(ChessBoard board, ChessPosition position) {
        System.out.println("Start q");
        for (ChessPosition position2 : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position2);
            if (piece != null)// Do something with the piece
                System.out.println("Position: " + position2 + ", Piece: " + piece + ", Piece type = " + piece.getPieceType() + ", Piece has moved = " + piece.hasMoved());
        }
        System.out.println("End q");

        // Preliminary check: If king is close to the edge, castling is not possible
        if (position.column() <= 2 || hasMoved(position)) return false;

        // Check if the path for castling is clear and safe
        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.row(), 1));
        if (rook == null || rook.getPieceType() != PieceType.ROOK) return false;

        System.out.println("Q RookHasMoved = " + rookHasMoved(board, position, false) + ", rook.hasMoved() = " + rook.hasMoved() + ", Rook = " + rook);

        // Check if rook has moved
        if (rookHasMoved(board, position, false)) return false;

        return IntStream.range(1, 4)
                .allMatch(i -> board.getPiece(new ChessPositionImpl(position.row(), position.column() - i)) == null
                        && !isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() - i)));

    }
}
