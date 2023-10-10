package chess;

import java.util.HashMap;
import java.util.Map;

public class ChessBoardImpl implements ChessBoard {

    private final Map<ChessPosition, ChessPiece> board;

    // Store the start and end positions of the last move
    private ChessPosition lastMoveStartPosition;
    private ChessPosition lastMoveEndPosition;

    public ChessBoardImpl() {
        board = new HashMap<>();
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        lastMoveStartPosition = lastMoveEndPosition;  // Update start position with the previous end position
        lastMoveEndPosition = position;               // Update end position with the current position
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
        ChessPiece removedPiece = board.remove(position);
        if (removedPiece != null) {
            lastMoveStartPosition = position;
            lastMoveEndPosition = null;  // No end position for a removed piece
        }
    }

    @Override
    public ChessPosition getLastMoveStartPosition() {
        return lastMoveStartPosition;
    }

    @Override
    public ChessPosition getLastMoveEndPosition() {
        return lastMoveEndPosition;
    }
}
