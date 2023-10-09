package chess;

import java.util.ArrayList;
import java.util.Collection;

public record QueenPiece(ChessGame.TeamColor teamColor) implements ChessPiece {

    @Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Combine the logic of Rook and Bishop
        RookPiece rook = new RookPiece(teamColor);
        BishopPiece bishop = new BishopPiece(teamColor);

        moves.addAll(rook.pieceMoves(board, myPosition));
        moves.addAll(bishop.pieceMoves(board, myPosition));

        return moves;
    }
}
