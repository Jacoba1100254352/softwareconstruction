package chess;

import java.util.ArrayList;
import java.util.Collection;

public record PawnPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

// Check for simple forward move
        int newRow = myPosition.row() + direction;
        if (newRow >= 1 && newRow <= 8) {
            ChessPosition forwardOne = new ChessPositionImpl(newRow, myPosition.column());
            if (board.getPiece(forwardOne) == null) {

                // Check for promotion
                if (forwardOne.row() == 1 || forwardOne.row() == 8) {
                    // Add all promotions
                    moves.add(new ChessMoveImpl(myPosition, forwardOne, PieceType.QUEEN, board));
                    moves.add(new ChessMoveImpl(myPosition, forwardOne, PieceType.BISHOP, board));
                    moves.add(new ChessMoveImpl(myPosition, forwardOne, PieceType.ROOK, board));
                    moves.add(new ChessMoveImpl(myPosition, forwardOne, PieceType.KNIGHT, board));
                } else {
                    moves.add(new ChessMoveImpl(myPosition, forwardOne, null, board));
                }

                // Check for double move for pawns on their starting rows
                if ((teamColor == ChessGame.TeamColor.WHITE && myPosition.row() == 2) ||
                        (teamColor == ChessGame.TeamColor.BLACK && myPosition.row() == 7)) {
                    ChessPosition forwardTwo = new ChessPositionImpl(myPosition.row() + (2 * direction), myPosition.column());
                    if (board.getPiece(forwardTwo) == null) {
                        moves.add(new ChessMoveImpl(myPosition, forwardTwo, null, board));
                    }
                }
            }
        }

        // Check for diagonal captures
        for (int diagDirection : new int[]{-1, 1}) {
            newRow = myPosition.row() + direction;
            int newCol = myPosition.column() + diagDirection;

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition diagonal = new ChessPositionImpl(newRow, newCol);
                ChessPiece pieceAtDiagonal = board.getPiece(diagonal);
                if (pieceAtDiagonal != null && pieceAtDiagonal.teamColor() != this.teamColor) {
                    // Check for promotion on capture
                    if (newRow == 1 || newRow == 8) {
                        moves.add(new ChessMoveImpl(myPosition, diagonal, PieceType.QUEEN, board));
                        moves.add(new ChessMoveImpl(myPosition, diagonal, PieceType.BISHOP, board));
                        moves.add(new ChessMoveImpl(myPosition, diagonal, PieceType.ROOK, board));
                        moves.add(new ChessMoveImpl(myPosition, diagonal, PieceType.KNIGHT, board));
                    } else {
                        moves.add(new ChessMoveImpl(myPosition, diagonal, null, board));
                    }
                }
            }
        }


        // Check for en passant captures
        for (int sideDirection : new int[]{-1, 1}) {
            int newCol = myPosition.column() + sideDirection;
            if (newCol < 1 || newCol > 8) {
                continue;  // Skip this iteration if the column is out of bounds
            }

            ChessPosition side = new ChessPositionImpl(myPosition.row(), newCol);
            ChessPiece pieceAtSide = board.getPiece(side);
            if (pieceAtSide instanceof PawnPiece && pieceAtSide.teamColor() != this.teamColor) {
                if (board.wasLastMoveTwoSquarePawnMove()) {
                    ChessPosition capturePos = new ChessPositionImpl(myPosition.row() + direction, myPosition.column() + sideDirection);
                    if (board.getPiece(capturePos) == null) {
                        moves.add(new ChessMoveImpl(myPosition, capturePos, null, board));
                    }
                }
            }
        }

        return moves;
    }
}
