package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGameImpl implements ChessGame {

    private ChessBoard board;
    private TeamColor currentTeamTurn;

    public ChessGameImpl() {
        board = new ChessBoardImpl();
        currentTeamTurn = TeamColor.WHITE;  // White always starts the game
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
        if (piece == null || piece.teamColor() != currentTeamTurn) {
            return new ArrayList<>();
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        // Filter out moves that would place the king in check
        Collection<ChessMove> filteredMoves = new ArrayList<>();

        // Find the position of the king for the current team
        ChessPosition kingPosition = null;
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece otherPiece = board.getPiece(position);
            if (otherPiece != null && otherPiece.teamColor() == currentTeamTurn && otherPiece.getPieceType() == ChessPiece.PieceType.KING) {
                kingPosition = position;
                break;
            }
        }

        for (ChessMove move : moves) {
            // Simulate the move
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            board.removePiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);

            boolean isCheck = false;

            // Check if the king would be under attack after the move
            for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
                ChessPiece attackingPiece = board.getPiece(position);
                if (attackingPiece != null && attackingPiece.teamColor() != currentTeamTurn) {
                    Collection<ChessMove> attackingMoves = attackingPiece.pieceMoves(board, position);
                    for (ChessMove attackingMove : attackingMoves) {
                        if (attackingMove.getEndPosition().equals(kingPosition)) {
                            isCheck = true;
                            break;
                        }
                    }
                }
                if (isCheck) break;
            }

            // Revert the move
            board.removePiece(move.getEndPosition());
            if (capturedPiece != null) {
                board.addPiece(move.getEndPosition(), capturedPiece);
            }
            board.addPiece(move.getStartPosition(), piece);

            if (!isCheck) {
                filteredMoves.add(move);
            }
        }

        return filteredMoves;
    }


    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.teamColor() != currentTeamTurn) {
            throw new InvalidMoveException("Invalid move.");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves.contains(move)) {
            board.removePiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);
            switchTeam();
        } else {
            throw new InvalidMoveException("Invalid move.");
        }
    }

    private void switchTeam() {
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        // Find the position of the king for the given teamColor
        ChessPosition kingPosition = null;
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                kingPosition = position;
                break;
            }
        }

        // If king's position is not found (which shouldn't happen), return false
        if (kingPosition == null) return false;

        // For every opposing piece on the board, check if any of their valid moves can capture the king
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() != teamColor) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, position);
                for (ChessMove move : validMoves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;  // King can be captured by this move, so it's in check
                    }
                }
            }
        }

        return false;  // If no opposing piece can capture the king, it's not in check
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        // If the team is not in check, they can't be in checkmate
        if (!isInCheck(teamColor)) return false;

        // Check all valid moves for all pieces of teamColor
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, position);
                for (ChessMove move : validMoves) {
                    // Simulate the move
                    ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
                    board.removePiece(move.getStartPosition());
                    board.addPiece(move.getEndPosition(), piece);

                    // Check if the team is still in check after the move
                    boolean stillInCheck = isInCheck(teamColor);

                    // Revert the move
                    board.removePiece(move.getEndPosition());
                    if (capturedPiece != null) {
                        board.addPiece(move.getEndPosition(), capturedPiece);
                    }
                    board.addPiece(move.getStartPosition(), piece);

                    // If the move gets the king out of check, it's not checkmate
                    if (!stillInCheck) return false;
                }
            }
        }

        // If none of the moves can get the king out of check, then it's checkmate
        return true;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        // If it's the given team's turn and they have no valid moves and they aren't in check, then it's a stalemate.
        if (teamColor != currentTeamTurn) {
            return false;
        }

        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor) {
                Collection<ChessMove> moves = validMoves(position);
                if (moves != null && !moves.isEmpty()) {
                    return false;  // There's at least one valid move
                }
            }
        }

        return !isInCheck(teamColor);  // It's a stalemate if the team isn't in check
    }

    @Override
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }
}