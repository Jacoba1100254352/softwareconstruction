package chess;

import java.util.ArrayList;
import java.util.Collection;

public record KnightPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.KNIGHT;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] knightMoves = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        for (int[] move : knightMoves) {
            int newRow = myPosition.row() + move[0];
            int newCol = myPosition.column() + move[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPositionImpl(newRow, newCol);
                ChessPiece pieceAtNewPos = board.getPiece(newPos);
                if (pieceAtNewPos == null || pieceAtNewPos.teamColor() != teamColor) {
                    moves.add(new ChessMoveImpl(myPosition, newPos, null, board));
                }
            }
        }
        return moves;
    }
}
