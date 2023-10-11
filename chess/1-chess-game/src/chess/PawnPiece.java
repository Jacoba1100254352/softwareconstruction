package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnPiece implements ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private boolean hasMoved = false;

    public PawnPiece(ChessGame.TeamColor teamColor) {
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

    private static final int BOARD_SIZE = 8;

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int newRow = myPosition.row() + direction;

        if (isValidRow(newRow)) {
            checkForwardMoves(board, myPosition, newRow, moves, direction);
            checkDiagonalCaptures(board, myPosition, newRow, moves);
            checkEnPassantCaptures(board, myPosition, moves, direction);
        }

        return moves;
    }

    @Override
    public boolean canAttack(ChessBoard board, ChessPosition from, ChessPosition to) {
        int rowDiff = to.row() - from.row();
        int colDiff = to.column() - from.column();

        if (teamColor == ChessGame.TeamColor.WHITE) {
            return rowDiff == -1 && Math.abs(colDiff) == 1;
        } else {
            return rowDiff == 1 && Math.abs(colDiff) == 1;
        }
    }


    private boolean isValidRow(int row) {
        return row >= 1 && row <= BOARD_SIZE;
    }

    private boolean isValidColumn(int col) {
        return col >= 1 && col <= BOARD_SIZE;
    }

    private void checkForwardMoves(ChessBoard board, ChessPosition myPosition, int newRow, Collection<ChessMove> moves, int direction) {
        ChessPosition forwardOne = new ChessPositionImpl(newRow, myPosition.column());
        if (board.getPiece(forwardOne) == null) {
            addMoveWithPromotion(myPosition, forwardOne, moves);

            if (isNewPawnPosition(myPosition) && board.getPiece(new ChessPositionImpl(myPosition.row() + (2 * direction), myPosition.column())) == null)
                moves.add(new ChessMoveImpl(myPosition, new ChessPositionImpl(myPosition.row() + (2 * direction), myPosition.column()), null));
        }
    }

    private boolean isNewPawnPosition(ChessPosition position) {
        return (teamColor == ChessGame.TeamColor.WHITE && position.row() == 2) || (teamColor == ChessGame.TeamColor.BLACK && position.row() == 7);
    }

    private void checkDiagonalCaptures(ChessBoard board, ChessPosition myPosition, int newRow, Collection<ChessMove> moves) {
        for (int diagDirection : new int[]{-1, 1}) {
            int newCol = myPosition.column() + diagDirection;

            if (isValidColumn(newCol)) {
                ChessPosition diagonal = new ChessPositionImpl(newRow, newCol);
                ChessPiece pieceAtDiagonal = board.getPiece(diagonal);
                if (pieceAtDiagonal != null && pieceAtDiagonal.teamColor() != this.teamColor)
                    addMoveWithPromotion(myPosition, diagonal, moves);
            }
        }
    }

    private void checkEnPassantCaptures(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int direction) {
        ChessMove lastMove = board.getLastMove();
        if (lastMove == null) return;

        ChessPiece lastPieceMoved = board.getPiece(lastMove.getEndPosition());
        if (lastPieceMoved == null || lastPieceMoved.getPieceType() != PieceType.PAWN) return;

        int doubleMoveRow = (teamColor == ChessGame.TeamColor.WHITE) ? 5 : 4;
        if (myPosition.row() != doubleMoveRow) return;

        for (int sideDirection : new int[]{-1, 1}) {
            int newCol = myPosition.column() + sideDirection;

            if (isValidColumn(newCol)) {
                ChessPosition side = new ChessPositionImpl(myPosition.row(), newCol);
                ChessPiece pieceAtSide = board.getPiece(side);

                if (pieceAtSide != null && pieceAtSide.equals(lastPieceMoved)) {
                    ChessPosition capturePos = new ChessPositionImpl(myPosition.row() + direction, newCol);
                    if (board.getPiece(capturePos) == null)
                        moves.add(new ChessMoveImpl(myPosition, capturePos, null));
                }
            }
        }
    }

    private void addMoveWithPromotion(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        if (end.row() == 1 || end.row() == BOARD_SIZE)
            promote(start, end, moves);
        else moves.add(new ChessMoveImpl(start, end, null));
    }

    private void promote(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        for (PieceType type : new PieceType[]{PieceType.QUEEN, PieceType.BISHOP, PieceType.ROOK, PieceType.KNIGHT})
            moves.add(new ChessMoveImpl(start, end, type));
    }
}
