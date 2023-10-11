package chess;

import java.util.HashMap;
import java.util.Map;

public class ChessBoardImpl implements ChessBoard {

    // Using a HashMap to store the position and corresponding piece
    private final Map<ChessPosition, ChessPiece> board;

    public ChessBoardImpl() {
        board = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = getPiece(new ChessPositionImpl(row, col));
                sb.append((piece == null) ? "." : piece.getPieceType().toString().charAt(0));
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    // To keep track of the last move
    private ChessMove lastMove;

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        lastMove = new ChessMoveImpl(lastMove != null ? lastMove.getEndPosition() : null, position, piece.getPieceType());
        board.put(position, piece);
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return board.get(position);
    }

    @Override
    public void resetBoard() {
        board.clear();

        // Setting up the pawns
        for (int col = 1; col <= 8; col++) {
            board.put(new ChessPositionImpl(2, col), new PawnPiece(ChessGame.TeamColor.WHITE));
            board.put(new ChessPositionImpl(7, col), new PawnPiece(ChessGame.TeamColor.BLACK));
        }

        // Setting up the rooks
        board.put(new ChessPositionImpl(1, 1), new RookPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(1, 8), new RookPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(8, 1), new RookPiece(ChessGame.TeamColor.BLACK));
        board.put(new ChessPositionImpl(8, 8), new RookPiece(ChessGame.TeamColor.BLACK));

        // Setting up the knights
        board.put(new ChessPositionImpl(1, 2), new KnightPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(1, 7), new KnightPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(8, 2), new KnightPiece(ChessGame.TeamColor.BLACK));
        board.put(new ChessPositionImpl(8, 7), new KnightPiece(ChessGame.TeamColor.BLACK));

        // Setting up the bishops
        board.put(new ChessPositionImpl(1, 3), new BishopPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(1, 6), new BishopPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(8, 3), new BishopPiece(ChessGame.TeamColor.BLACK));
        board.put(new ChessPositionImpl(8, 6), new BishopPiece(ChessGame.TeamColor.BLACK));

        // Setting up the queens
        board.put(new ChessPositionImpl(1, 4), new QueenPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(8, 4), new QueenPiece(ChessGame.TeamColor.BLACK));

        // Setting up the kings
        board.put(new ChessPositionImpl(1, 5), new KingPiece(ChessGame.TeamColor.WHITE));
        board.put(new ChessPositionImpl(8, 5), new KingPiece(ChessGame.TeamColor.BLACK));
    }

    @Override
    public void removePiece(ChessPosition position) {
        ChessPiece removedPiece = board.get(position);
        if (removedPiece != null)
            lastMove = new ChessMoveImpl(position, null, removedPiece.getPieceType());
        board.remove(position);
    }

    // New method to check for two-square pawn move
    public boolean wasLastMoveTwoSquarePawnMove() {
        if (lastMove == null) return false;
        if (lastMove.getPromotionPiece() != ChessPiece.PieceType.PAWN) return false;
        ChessPosition startPos = lastMove.getStartPosition();
        ChessPosition endPos = lastMove.getEndPosition();
        if (startPos == null || endPos == null) return false;  // Added this check
        return Math.abs(startPos.row() - endPos.row()) == 2;
    }
}
