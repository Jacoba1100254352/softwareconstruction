package chess;

import java.util.ArrayList;
import java.util.Collection;

public record KingPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.KING;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Potential moves for a king, representing one square in every direction
        int[][] directions = {
                {1, 0}, {1, 1}, {0, 1}, {-1, 1},
                {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
        };

        for (int[] direction : directions) {
            int newRow = myPosition.row() + direction[0];
            int newCol = myPosition.column() + direction[1];

            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                // Move is outside the bounds of the board
                continue;
            }

            ChessPosition newPosition = new ChessPositionImpl(newRow, newCol);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            if (pieceAtNewPosition == null || pieceAtNewPosition.teamColor() != this.teamColor()) {
                // Either the square is empty, or there's an opponent's piece that can be captured
                moves.add(new ChessMoveImpl(myPosition, newPosition, null, board));
            }
        }

        // Castling
        if (canCastleKingSide(board, myPosition)) {
            moves.add(new ChessMoveImpl(myPosition, new ChessPositionImpl(myPosition.row(), myPosition.column() + 2), null, board));
        }
        if (canCastleQueenSide(board, myPosition)) {
            moves.add(new ChessMoveImpl(myPosition, new ChessPositionImpl(myPosition.row(), myPosition.column() - 2), null, board));
        }

        return moves;
    }

    private boolean isSquareUnderAttack(ChessBoard board, ChessPosition position, ChessGame.TeamColor enemyColor) {
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece != null && piece.teamColor() == enemyColor) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, pos);
                for (ChessMove move : validMoves) {
                    if (move.getEndPosition().equals(position)) {
                        return true;  // The square is under attack
                    }
                }
            }
        }
        return false;
    }

    private boolean canCastleKingSide(ChessBoard board, ChessPosition position) {
        if (position.column() >= 7) {
            return false;
        }

        ChessGame.TeamColor enemyColor = (this.teamColor() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (board.getPiece(new ChessPositionImpl(position.row(), position.column() + 1)) != null
                || board.getPiece(new ChessPositionImpl(position.row(), position.column() + 2)) != null
                || isSquareUnderAttack(board, position, enemyColor)
                || isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() + 1), enemyColor)
                || isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() + 2), enemyColor)) {
            return false;
        }

        // Check if the king and the rook have not moved.
        if (hasMoved(position)) return false;

        ChessPiece kingSideRook = board.getPiece(new ChessPositionImpl(position.row(), 8));
        return kingSideRook != null && kingSideRook.getPieceType() == PieceType.ROOK;
    }

    private boolean hasMoved(ChessPosition position) {
        if (this.teamColor() == ChessGame.TeamColor.WHITE && position.row() != 1 && position.column() != 5) {
            return true;
        }
        if (this.teamColor() == ChessGame.TeamColor.BLACK && position.row() != 8 && position.column() != 5) {
            return true;
        }
        return false;
    }

    private boolean canCastleQueenSide(ChessBoard board, ChessPosition position) {
        if (position.column() <= 2) {
            return false;
        }

        ChessGame.TeamColor enemyColor = (this.teamColor() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (board.getPiece(new ChessPositionImpl(position.row(), position.column() - 1)) != null
                || board.getPiece(new ChessPositionImpl(position.row(), position.column() - 2)) != null
                || board.getPiece(new ChessPositionImpl(position.row(), position.column() - 3)) != null
                || isSquareUnderAttack(board, position, enemyColor)
                || isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() - 1), enemyColor)
                || isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() - 2), enemyColor)) {
            return false;
        }

        // Check if the king and the rook have not moved.
        if (hasMoved(position)) return false;

        ChessPiece queenSideRook = board.getPiece(new ChessPositionImpl(position.row(), 1));
        return queenSideRook != null && queenSideRook.getPieceType() == PieceType.ROOK;
    }
}
