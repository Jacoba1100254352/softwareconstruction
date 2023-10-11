package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ChessGameImpl implements ChessGame {

    private ChessBoard board;
    private TeamColor currentTeamTurn;

    public ChessGameImpl() {
        // Initializing a fresh board and setting starting team to WHITE
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

    private boolean isSquareUnderAttack(ChessPosition position) {
        // Determine the enemy color based on the current team color
        TeamColor enemyColor = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // Iterate over all possible positions on the board
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);

            // Check if there is a piece on the current position and if it belongs to the enemy
            if (piece != null && piece.teamColor() == enemyColor) {
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);

                // If the piece is a king, filter out long-range moves
                if (piece.getPieceType() == ChessPiece.PieceType.KING)
                    moves.removeIf(move -> Math.abs(move.getEndPosition().column() - move.getStartPosition().column()) > 1);

                // If any of the moves target the position in question, it is under attack
                if (moves.stream().anyMatch(move -> move.getEndPosition().equals(position)))
                    return true;
            }
        }
        return false;
    }

    private boolean canCastle(ChessBoard board, ChessPosition position, boolean kingSide) {
        // Check for the king's current state and position
        ChessPiece king = board.getPiece(findCurrentKingsPosition());

        // Validate conditions for castling (king's position, movement history, and type)
        if (king == null || king.hasMoved())
            return false;

        // Determine rook's position based on the side of castling
        ChessPosition rookPosition = new ChessPositionImpl(position.row(), kingSide ? 8 : 1);
        ChessPiece rook = board.getPiece(rookPosition);

        // Validate rook's state for castling
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved())
            return false;

        // Ensure path between king and rook is clear and not under attack
        int[] range = kingSide ? new int[]{1, 2} : new int[]{1, 2, 3};
        for (int i : range) {
            ChessPosition checkPos = new ChessPositionImpl(position.row(), position.column() + (kingSide ? i : -i));

            // If any square between king and rook is occupied or under attack, castling is invalid
            if (board.getPiece(checkPos) != null || isSquareUnderAttack(checkPos))
                return false;
        }
        return true;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);

        // If no piece found or piece does not belong to the current team, return an empty list
        if (piece == null || piece.teamColor() != currentTeamTurn) return new ArrayList<>();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        // Filter out moves that would result in a check for the current team
        Collection<ChessMove> validMoves = moves.stream()
                .filter(move -> !doesMoveResultInCheck(move, piece))
                .collect(Collectors.toList());

        // Additional logic to account for castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (canCastle(board, startPosition, true))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() + 2), null));
            if (canCastle(board, startPosition, false))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() - 2), null));
        }

        return validMoves;
    }

    private boolean doesMoveResultInCheck(ChessMove move, ChessPiece piece) {
        // Remember the piece on the target position
        ChessPiece originalEndPiece = board.getPiece(move.getEndPosition());

        // Temporarily apply the move
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());

        // If we're moving the king, update the king's position
        ChessPosition kingPosition = (piece.getPieceType() == ChessPiece.PieceType.KING) ? move.getEndPosition() : findCurrentKingsPosition();

        // Check if the current team's king is under attack after the move
        boolean isCheck = isSquareUnderAttack(kingPosition);

        // Revert the move
        board.addPiece(move.getStartPosition(), piece);
        if (originalEndPiece != null) board.addPiece(move.getEndPosition(), originalEndPiece);
        else board.removePiece(move.getEndPosition());

        return isCheck;
    }

    private ChessPosition findCurrentKingsPosition() {
        // Search for the king's position on the board
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == currentTeamTurn && piece.getPieceType() == ChessPiece.PieceType.KING)
                return position;
        }
        return null;
    }

    private ChessPosition[] findRooks() {
        ChessPosition[] rookPositions = {null, null};
        int size = 0;
        // Search for the rook's position on the board
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                if (piece.teamColor() == currentTeamTurn)
                    rookPositions[size++] = position;
            }
        }
        return rookPositions;
    }

    /**
     * Executes the given chess move on the board.
     * Handles special moves like castling and pawn promotion.
     *
     * @param move The move to be executed.
     */
    private void executeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        piece.markAsMoved();
        board.removePiece(move.getStartPosition());

        // Check if this move is a special king's castling move.
        if (piece.getPieceType() == ChessPiece.PieceType.KING && isCastlingMove(move)) {
            handleCastling(move);
        } // Check if this move is a pawn promotion move.
        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            piece = promotePawn(move);
        }

        board.addPiece(move.getEndPosition(), piece);
    }

    /**
     * Determines if the given move is a king's castling move.
     * A castling move involves moving the king 2 squares.
     *
     * @param move The move to be checked.
     * @return true if it's a castling move, false otherwise.
     */
    private boolean isCastlingMove(ChessMove move) {
        return Math.abs(move.getEndPosition().column() - move.getStartPosition().column()) == 2;
    }

    /**
     * Handles the king's castling move.
     * This involves moving the rook to its new position after the king's castling move.
     *
     * @param move The castling move.
     */
    private void handleCastling(ChessMove move) {
        ChessPosition[] rookPositions = findRooks();
        int colDiff = move.getEndPosition().column() - move.getStartPosition().column();

        for (ChessPosition rookPosition : rookPositions) {
            if (isValidRookForCastling(rookPosition, colDiff)) {
                ChessPosition rookNewPosition = getRookNewPositionAfterCastling(move, colDiff);
                moveRookForCastling(rookPosition, rookNewPosition);
                break;
            }
        }
    }

    /**
     * Determines if the rook at the given position is valid for castling.
     *
     * @param rookPosition The position of the rook.
     * @param colDiff The column difference between the start and end positions of the king's move.
     * @return true if the rook is valid for castling, false otherwise.
     */
    private boolean isValidRookForCastling(ChessPosition rookPosition, int colDiff) {
        ChessPiece rook = board.getPiece(rookPosition);
        return rookPosition != null &&
                ((colDiff == 2 && rookPosition.column() == 8) || (colDiff == -2 && rookPosition.column() == 1)) &&
                rook != null && !rook.hasMoved();
    }

    /**
     * Gets the new position of the rook after the king's castling move.
     *
     * @param move The king's castling move.
     * @param colDiff The column difference between the start and end positions of the king's move.
     * @return The new position for the rook.
     */
    private ChessPosition getRookNewPositionAfterCastling(ChessMove move, int colDiff) {
        return (colDiff == 2) ?
                new ChessPositionImpl(move.getStartPosition().row(), 6) :
                new ChessPositionImpl(move.getStartPosition().row(), 4);
    }

    /**
     * Moves the rook to its new position after the king's castling move.
     *
     * @param originalPosition The original position of the rook.
     * @param newPosition The new position for the rook after castling.
     */
    private void moveRookForCastling(ChessPosition originalPosition, ChessPosition newPosition) {
        ChessPiece rook = board.getPiece(originalPosition);
        board.removePiece(originalPosition);
        board.addPiece(newPosition, rook);
        rook.markAsMoved();
    }

    /**
     * Handles the promotion of a pawn.
     * Replaces the pawn with the piece specified in the move (e.g., queen, rook, bishop, knight).
     *
     * @param move The pawn promotion move.
     * @return The new chess piece after pawn promotion.
     */
    private ChessPiece promotePawn(ChessMove move) {
        ChessPiece piece = switch (move.getPromotionPiece()) {
            case QUEEN -> new QueenPiece(currentTeamTurn);
            case ROOK -> new RookPiece(currentTeamTurn);
            case BISHOP -> new BishopPiece(currentTeamTurn);
            case KNIGHT -> new KnightPiece(currentTeamTurn);
            default -> throw new IllegalArgumentException("Invalid promotion piece");
        };
        piece.markAsMoved();
        return piece;
    }



    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Validate and execute the move, then switch the current team
        if (validMoves(move.getStartPosition()).contains(move)) {
            executeMove(move);
            switchTeam();
        } else throw new InvalidMoveException("Invalid move.");
    }

    private void switchTeam() {
        // Toggle between BLACK and WHITE
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        // Check if the team's king is under attack
        return isSquareUnderAttack(findCurrentKingsPosition());
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        // Check if it's the current team's turn and if they have any valid moves
        if (teamColor != currentTeamTurn) return false;

        return ChessPositionImpl.getAllPositions().stream().noneMatch(pos -> {
            ChessPiece piece = board.getPiece(pos);
            return piece != null && piece.teamColor() == teamColor && !validMoves(pos).isEmpty();
        });
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        // Check if the team's king is in check and if they have any valid moves
        return isInCheck(teamColor) && ChessPositionImpl.getAllPositions().stream().noneMatch(pos -> {
            ChessPiece piece = board.getPiece(pos);
            return piece != null && piece.teamColor() == teamColor && !validMoves(pos).isEmpty();
        });
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