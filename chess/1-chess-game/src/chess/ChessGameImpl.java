package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGameImpl implements ChessGame {
    private ChessBoard board;
    private TeamColor currentTeamTurn;

    public ChessGameImpl() {
        board = new ChessBoardImpl();
        currentTeamTurn = TeamColor.WHITE;
    }

    @Override
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.teamColor() != currentTeamTurn)
            return new ArrayList<>();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            if (moveWouldPutOwnKingInCheck(move, piece))
                continue;
            validMoves.add(move);
        }

        return validMoves;
    }

    private boolean moveWouldPutOwnKingInCheck(ChessMove move, ChessPiece piece) {
        ChessBoard clonedBoard = board.clone();
        ChessPiece clonedPiece = piece.clone();
        clonedBoard.removePiece(move.getStartPosition());
        clonedBoard.addPiece(move.getEndPosition(), clonedPiece);

        ChessPosition kingPosition = findKingPosition(clonedBoard, clonedPiece.teamColor());
        return isPositionUnderAttack(kingPosition, clonedPiece.teamColor());
    }

    private ChessPosition findKingPosition(ChessBoard board, TeamColor color) {
        for (int rank = 1; rank <= 8; rank++) {
            for (int file = 1; file <= 8; file++) {
                ChessPosition position = new ChessPositionImpl(rank, file);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.teamColor() == color && piece.getPieceType() == ChessPiece.PieceType.KING)
                    return position;
            }
        }
        throw new IllegalStateException("King not found on the board!");
    }

    private boolean isPositionUnderAttack(ChessPosition position, TeamColor defendingTeam) {
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece attackingPiece = board.getPiece(pos);
            if (attackingPiece != null && attackingPiece.teamColor() != defendingTeam) {
                for (ChessMove attackingMove : attackingPiece.pieceMoves(board, pos)) {
                    if (attackingMove.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()).contains(move)) {
            ChessPiece pieceBeingMoved = board.getPiece(move.getStartPosition());
            if (pieceBeingMoved != null && !pieceBeingMoved.hasMoved())
                pieceBeingMoved.markAsMoved();

            board.removePiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), pieceBeingMoved);
            switchTeam();
        } else throw new InvalidMoveException("Invalid move.");
    }

    private void switchTeam() {
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(board, teamColor);
        return isPositionUnderAttack(kingPosition, teamColor);
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor != currentTeamTurn || isInCheck(teamColor)) return false;

        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor && !validMoves(position).isEmpty()) {
                return false;
            }
        }

        return true;
    }


    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;

        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor && !validMoves(position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setBoard(ChessBoard board) {
        // Add validation here if needed
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        return board.clone();
    }
}