package chess;

import java.util.HashMap;
import java.util.Map;

public class ChessBoardImpl implements ChessBoard {

    // Using a HashMap to store the position and corresponding piece
    private final Map<ChessPosition, ChessPiece> board;

    // To keep track of the last move
    private ChessMove lastMove;

    // For manual/full board setup and testing. Set to true unless full-board setup with resetBoard()
    private boolean testingMode;

    public ChessBoardImpl() {
        board = new HashMap<>();
        testingMode = true;
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

    @Override
    public void resetBoard() {
        board.clear();
        testingMode = false;

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
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);
    }

    @Override
    public void removePiece(ChessPosition position) {
        board.remove(position);
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return board.get(position);
    }

    @Override
    public ChessMove getLastMove() {
        return lastMove;
    }

    @Override
    public void setLastMove(ChessMove lastMove) {
        this.lastMove = lastMove;
    }

    @Override
    public boolean getTestingMode() {
        return testingMode;
    }

    public Map<ChessPosition, ChessPiece> getBoard() {
        return board;
    }
}
