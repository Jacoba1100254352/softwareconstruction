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
        if (piece == null || piece.teamColor() != currentTeamTurn)
            return new ArrayList<>();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        ChessPosition kingPosition = findKingPosition(currentTeamTurn);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : moves)
            if (!doesMoveResultInCheck(move, kingPosition, piece))
                validMoves.add(move);

        return validMoves;
    }

    private boolean doesMoveResultInCheck(ChessMove move, ChessPosition kingPosition, ChessPiece piece) {
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);

        if (piece.getPieceType() == ChessPiece.PieceType.KING)
            kingPosition = move.getEndPosition();

        boolean isCheck = isPositionUnderAttack(kingPosition, currentTeamTurn);

        board.removePiece(move.getEndPosition());
        if (capturedPiece != null) board.addPiece(move.getEndPosition(), capturedPiece);
        board.addPiece(move.getStartPosition(), piece);

        return isCheck;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING)
                return position;
        }
        return null;  // This should never happen
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
            executeMove(move);
            switchTeam();
        } else throw new InvalidMoveException("Invalid move.");
    }

    private void executeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        // Mark the piece as having moved
        piece.markAsMoved();

        board.removePiece(move.getStartPosition());

        // Detect if the move is a castling move
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int colDiff = move.getEndPosition().column() - move.getStartPosition().column();
            if (Math.abs(colDiff) == 2) {
                ChessPosition rookOriginalPosition;
                ChessPosition rookNewPosition;



                // If moving 2 squares to the right, it's king-side castling
                if (colDiff == 2) {
                    rookOriginalPosition = new ChessPositionImpl(move.getStartPosition().row(), 8);  // Rook's original position
                    rookNewPosition = new ChessPositionImpl(move.getStartPosition().row(), 6);  // Rook's new position
                } else {
                    rookOriginalPosition = new ChessPositionImpl(move.getStartPosition().row(), 1);  // Rook's original position
                    rookNewPosition = new ChessPositionImpl(move.getStartPosition().row(), 4);  // Rook's new position
                }


                ChessPiece rook = board.getPiece(rookOriginalPosition);
                board.removePiece(rookOriginalPosition);  // Remove rook from its original position
                board.addPiece(rookNewPosition, rook);  // Place rook at its new position
                rook.markAsMoved();
            }
        }

        // Check for pawn promotion
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            piece = switch (move.getPromotionPiece()) {
                case QUEEN -> new QueenPiece(currentTeamTurn);
                case ROOK -> new RookPiece(currentTeamTurn);
                case BISHOP -> new BishopPiece(currentTeamTurn);
                case KNIGHT -> new KnightPiece(currentTeamTurn);
                default -> piece;
            };
        }

        // Place the moving piece (king, pawn after promotion, or any other piece) at its new position
        board.addPiece(move.getEndPosition(), piece);

        System.out.println("Start Move");
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            piece = board.getPiece(position);
            if (piece != null)// Do something with the piece
                System.out.println("Position: " + position + ", Piece: " + piece + ", Piece type = " + piece.getPieceType() + ", Piece has moved = " + piece.hasMoved());
        }
        System.out.println("End Move");


    }


    private void switchTeam() {
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        return kingPosition != null && isPositionUnderAttack(kingPosition, teamColor);
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
    public void setBoard(ChessBoard board) {
        // Add validation here if needed
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        // Return a deep copy or read-only view if needed
        return board;
    }
}
