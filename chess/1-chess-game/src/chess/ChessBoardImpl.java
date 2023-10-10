package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChessBoardImpl implements ChessBoard, Cloneable {

    private Map<ChessPosition, ChessPiece> board;

    // Store the start and end positions of the last move
    private ChessPosition lastMoveStartPosition;
    private ChessPosition lastMoveEndPosition;

    public ChessBoardImpl() {
        board = new HashMap<>();
    }

    @Override
    public ChessBoardImpl clone() {
        try {
            ChessBoardImpl clonedBoard = (ChessBoardImpl) super.clone();
            clonedBoard.board = new HashMap<>();
            for (Map.Entry<ChessPosition, ChessPiece> entry : this.board.entrySet()) {
                ChessPosition clonedPosition = new ChessPositionImpl(entry.getKey().row(), entry.getKey().column());
                ChessPiece clonedPiece = entry.getValue().clone();
                clonedBoard.addPiece(clonedPosition, clonedPiece);
            }
            return clonedBoard;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();  // Should never happen
        }
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


    public boolean isSquareUnderThreat(ChessPosition position, ChessGame.TeamColor teamColor) {
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece checkingPiece = getPiece(pos);
            if (checkingPiece != null && checkingPiece.teamColor() != teamColor) {
                if (checkingPiece.getPieceType() != ChessPiece.PieceType.KING) {
                    Collection<ChessMove> threateningMoves = checkingPiece.pieceMoves(this, pos);
                    for (ChessMove move : threateningMoves) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    @Override
    public ChessPosition getKingPosition(ChessGame.TeamColor teamColor) {
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = getPiece(pos);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.teamColor() == teamColor) {
                return pos;
            }
        }
        return null; // This should never be reached unless something is wrong with the board state.
    }

}
