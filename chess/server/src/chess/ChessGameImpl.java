package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

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

    /**
     * Checks if a given square is under attack by the enemy team.
     *
     * @param position  The position to check.
     * @param teamColor The current team color.
     * @return true if the square is under attack, false otherwise.
     */
    private boolean isSquareUnderAttack(ChessPosition position, TeamColor teamColor) {
        // Determine enemy color
        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // Iterate through all positions on the board
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);

            // Check if piece exists, belongs to the enemy and can attack the position
            if (piece != null && piece.teamColor() == enemyColor) {
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);

                // If the piece is a king, limit its movement to 1 square
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    moves.removeIf(move -> Math.abs(move.getEndPosition().getCol() - move.getStartPosition().getCol()) > 1);
                }

                // Check if any of the moves can attack the given position
                if (moves.stream().anyMatch(move -> move.getEndPosition().equals(position)))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given team can castle.
     *
     * @param board    The current chess board.
     * @param position The king's position.
     * @param color    The king's color.
     * @param kingSide true if checking king-side castling, false if queen-side.
     * @return true if castling is possible, false otherwise.
     */
    private boolean canCastle(ChessBoard board, ChessPosition position, TeamColor color, boolean kingSide) {
        // Fetch the king piece and validate its conditions for castling
        ChessPiece king = board.getPiece(findCurrentKingsPosition(color));
        if (king == null || king.getPieceType() != ChessPiece.PieceType.KING || king.hasMoved())
            return false;

        // Fetch the rook piece and validate its conditions for castling
        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.getRow(), kingSide ? 8 : 1));
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved())
            return false;

        // Ensure there are no pieces in between the king and rook and the path isn't under attack
        int[] range = kingSide ? new int[]{1, 2} : new int[]{1, 2, 3};
        for (int i : range) {
            ChessPosition checkPos = new ChessPositionImpl(position.getRow(), position.getCol() + (kingSide ? i : -i));
            if (board.getPiece(checkPos) != null || isSquareUnderAttack(checkPos, color))
                return false;
        }
        return true;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        // Ensure the move isn't out of turn or not for testing purposes
        if (piece == null || (piece.teamColor() != currentTeamTurn && !board.getTestingMode()))
            return new ArrayList<>();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = moves.stream()
                .filter(move -> !doesMoveResultInCheck(move, piece))
                .collect(Collectors.toList());

        // Handle the En Passant logic for pawns
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN)
            validMoves.addAll(checkEnPassantCaptures(startPosition));

        // Handle castling logic for the king
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (canCastle(board, startPosition, piece.teamColor(), true))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.getRow(), startPosition.getCol() + 2), null));
            if (canCastle(board, startPosition, piece.teamColor(), false))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.getRow(), startPosition.getCol() - 2), null));
        }

        return validMoves;
    }

    /**
     * Checks for valid En Passant captures from a given pawn position.
     *
     * @param startPosition The position of the pawn.
     * @return A collection of valid En Passant captures.
     */
    private Collection<ChessMove> checkEnPassantCaptures(ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(startPosition);
        int direction = (currentPiece.teamColor() == TeamColor.WHITE) ? 1 : -1;

        // Ensure the pawn is in the right rank for En Passant
        if ((currentPiece.teamColor() == TeamColor.WHITE && startPosition.getRow() == 5) ||
                (currentPiece.teamColor() == TeamColor.BLACK && startPosition.getRow() == 4)) {

            // Check adjacent squares for enemy pawns that moved two squares in the last move
            for (int colDirection : new int[]{-1, 1}) {
                int column = startPosition.getCol() + colDirection;
                if (column < 1 || column > 7) continue;
                ChessPosition adjPos = new ChessPositionImpl(startPosition.getRow(), column);
                ChessPiece adjPiece = board.getPiece(adjPos);

                // Validate if the adjacent piece is an enemy pawn that moved two squares
                if (adjPiece != null && adjPiece.getPieceType() == ChessPiece.PieceType.PAWN && adjPiece.teamColor() != currentPiece.teamColor()) {
                    ChessMove lastMove = board.getLastMove();
                    if (lastMove != null && lastMove.getEndPosition().equals(adjPos) &&
                            Math.abs(lastMove.getStartPosition().getRow() - lastMove.getEndPosition().getRow()) == 2) {
                        ChessPosition endPosition = new ChessPositionImpl(startPosition.getRow() + direction, startPosition.getCol() + colDirection);
                        moves.add(new ChessMoveImpl(startPosition, endPosition, null));
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Checks if executing a given move would result in a check for the current team.
     *
     * @param move  The move to check.
     * @param piece The piece being moved.
     * @return true if the move results in check, false otherwise.
     */
    private boolean doesMoveResultInCheck(ChessMove move, ChessPiece piece) {
        // Temporarily apply the move to see if it results in check
        ChessPiece originalEndPiece = board.getPiece(move.getEndPosition());
        ChessPiece capturedPawn = null;

        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());


        // Find the position of the current team's king
        ChessPosition kingPosition = (piece.getPieceType() == ChessPiece.PieceType.KING) ? move.getEndPosition() : findCurrentKingsPosition(piece.teamColor());

        // Determine if the king is under attack
        boolean isCheck = isSquareUnderAttack(kingPosition, piece.teamColor());

        // Revert the move
        board.addPiece(move.getStartPosition(), piece);
        if (originalEndPiece != null) board.addPiece(move.getEndPosition(), originalEndPiece);
        else board.removePiece(move.getEndPosition());

        return isCheck;
    }

    /**
     * Finds the position of the current team's king on the board.
     *
     * @param teamColor The color of the king to find.
     * @return The position of the king or null if not found.
     */
    private ChessPosition findCurrentKingsPosition(TeamColor teamColor) {
        // Iterate through all positions to find the king
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING)
                return position;
        }
        return null;
    }

    /**
     * Executes a given move on the board.
     *
     * @param move The move to execute.
     */
    private void executeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        piece.markAsMoved();

        // Handle En Passant logic for pawns
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getEndPosition() != null &&
                Math.abs(move.getEndPosition().getCol() - move.getStartPosition().getCol()) == 1 &&
                board.getPiece(move.getEndPosition()) == null) {
            // Move the piece that is capturing
            ChessPosition capturingPawnPos = new ChessPositionImpl(move.getEndPosition().getRow(), move.getEndPosition().getCol());
            board.removePiece(capturingPawnPos);

            // Remove the captured piece
            ChessPosition capturedPawnPos = new ChessPositionImpl(move.getStartPosition().getRow(), move.getEndPosition().getCol());
            board.removePiece(capturedPawnPos);
        }

        // Handle castling logic for the king
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int colDiff = move.getEndPosition().getCol() - move.getStartPosition().getCol();
            if (Math.abs(colDiff) == 2) {
                ChessPosition rookOriginalPosition = (colDiff == 2) ?
                        new ChessPositionImpl(move.getStartPosition().getRow(), 8) :
                        new ChessPositionImpl(move.getStartPosition().getRow(), 1);

                ChessPosition rookNewPosition = (colDiff == 2) ?
                        new ChessPositionImpl(move.getStartPosition().getRow(), 6) :
                        new ChessPositionImpl(move.getStartPosition().getRow(), 4);

                ChessPiece rook = board.getPiece(rookOriginalPosition);
                board.removePiece(rookOriginalPosition);
                board.addPiece(rookNewPosition, rook);
                rook.markAsMoved();
            }
        }

        // Handle pawn promotion logic
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            piece = switch (move.getPromotionPiece()) {
                case QUEEN -> new QueenPiece(currentTeamTurn);
                case ROOK -> new RookPiece(currentTeamTurn);
                case BISHOP -> new BishopPiece(currentTeamTurn);
                case KNIGHT -> new KnightPiece(currentTeamTurn);
                default -> piece;
            };
        }


        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Update the current team's turn if in testing mode
        if (board.getTestingMode())
            currentTeamTurn = board.getPiece(move.getStartPosition()).teamColor();

        // Ensure the move is valid
        if (validMoves(move.getStartPosition()).contains(move)) {
            board.setLastMove(move);
            executeMove(move);

            if (!board.getTestingMode())
                // Switches the turn to the other team.
                currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        } else throw new InvalidMoveException("Invalid move.");
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        return isSquareUnderAttack(findCurrentKingsPosition(teamColor), teamColor);
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        // Ensure it's the current team's turn
        if (teamColor != currentTeamTurn) return false;

        // Check if no valid moves are available for any piece
        return ChessPositionImpl.getAllPositions().stream().noneMatch(pos -> {
            ChessPiece piece = board.getPiece(pos);
            return piece != null && piece.teamColor() == teamColor && !validMoves(pos).isEmpty();
        });
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        // Ensure the king is in check and no valid moves are available for any piece
        return isInCheck(teamColor) && ChessPositionImpl.getAllPositions().stream().noneMatch(pos -> {
            ChessPiece piece = board.getPiece(pos);
            return piece != null && piece.teamColor() == teamColor && !validMoves(pos).isEmpty();
        });
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public void setBoard(ChessBoard board) {
        this.board = board;
    }
}