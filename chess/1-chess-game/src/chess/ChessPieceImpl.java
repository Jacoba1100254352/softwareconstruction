package chess;

import java.util.Collection;

public class ChessPieceImpl implements ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;
    private final ChessPiece actualPiece;

    public ChessPieceImpl(ChessGame.TeamColor teamColor, PieceType pieceType) {
        this.teamColor = teamColor;
        this.pieceType = pieceType;

        switch (pieceType) {
            case KING -> this.actualPiece = new KingPiece(teamColor);
            case QUEEN -> this.actualPiece = new QueenPiece(teamColor);
            case BISHOP -> this.actualPiece = new BishopPiece(teamColor);
            case KNIGHT -> this.actualPiece = new KnightPiece(teamColor);
            case ROOK -> this.actualPiece = new RookPiece(teamColor);
            case PAWN -> this.actualPiece = new PawnPiece(teamColor);
            default -> throw new IllegalArgumentException("Unknown piece type: " + pieceType);
        }
    }

    @Override
    public ChessGame.TeamColor teamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return pieceType;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return actualPiece.pieceMoves(board, myPosition);
    }
}
