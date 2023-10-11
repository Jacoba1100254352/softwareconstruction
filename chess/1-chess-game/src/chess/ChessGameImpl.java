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

    private boolean isSquareUnderAttack(ChessPosition position, TeamColor teamColor) {
        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece != null && piece.teamColor() == enemyColor) {
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    moves.removeIf(move -> Math.abs(move.getEndPosition().column() - move.getStartPosition().column()) > 1);
                }
                if (moves.stream().anyMatch(move -> move.getEndPosition().equals(position)))
                    return true;
            }
        }
        return false;
    }

    private boolean canCastle(ChessBoard board, ChessPosition position, TeamColor color, boolean kingSide) {
        ChessPiece king = board.getPiece(findCurrentKingsPosition(color));
        if (king == null || king.getPieceType() != ChessPiece.PieceType.KING || king.teamColor() != color || king.hasMoved())
            return false;

        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.row(), kingSide ? 8 : 1));
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved())
            return false;

        int[] range = kingSide ? new int[]{1, 2} : new int[]{1, 2, 3};
        for (int i : range) {
            ChessPosition checkPos = new ChessPositionImpl(position.row(), position.column() + (kingSide ? i : -i));
            if (board.getPiece(checkPos) != null || isSquareUnderAttack(checkPos, color))
                return false;
        }
        return true;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        // Make sure that if an out of turn move is made it isn't for testing purposes and is truly a user error to catch
        if (piece == null || (piece.teamColor() != currentTeamTurn && !board.getTestingMode()))
            return new ArrayList<>();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        System.out.println("Moves before filtering: " + moves);
        Collection<ChessMove> validMoves = moves.stream()
                .filter(move -> !doesMoveResultInCheck(move, piece))
                .collect(Collectors.toList());
        System.out.println("Moves after filtering: " + validMoves);

        // En Passant logic
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN)
            validMoves.addAll(checkEnPassantCaptures(startPosition));

        System.out.println("Moves after En Passant check: " + validMoves);

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (canCastle(board, startPosition, piece.teamColor(), true))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() + 2), null));
            if (canCastle(board, startPosition, piece.teamColor(), false))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() - 2), null));
        }

        return validMoves;
    }

    private Collection<ChessMove> checkEnPassantCaptures(ChessPosition startPosition) {
        System.out.println("Checking En Passant for pawn at: " + startPosition);
        Collection<ChessMove> enPassantMoves = new ArrayList<>();

        // Get the last move made on the board
        ChessMove lastMove = board.getLastMove();
        if (lastMove == null) return enPassantMoves;

        ChessPiece lastPieceMoved = board.getPiece(lastMove.getEndPosition());
        if (lastPieceMoved == null || lastPieceMoved.getPieceType() != ChessPiece.PieceType.PAWN) return enPassantMoves;

        // Determine the row where a pawn can perform En Passant based on the OPPOSITE team's turn (since it's capturing the last moved pawn)
        int doubleMoveRow = (currentTeamTurn == ChessGame.TeamColor.WHITE) ? 4 : 5;

        // If the pawn isn't on the right row, return empty moves
        if (startPosition.row() != doubleMoveRow) return enPassantMoves;

        // Check both left (-1) and right (1) adjacent squares
        for (int sideDirection : new int[]{-1, 1}) {
            System.out.println(sideDirection);
            int newCol = startPosition.column() + sideDirection;
            System.out.println("newCol = " + newCol);

            // Ensure the column is within the board's bounds
            if (newCol < 1 || newCol > 8) continue;

            // If there's a pawn on the side that was the last piece moved
            ChessPosition sidePosition = new ChessPositionImpl(startPosition.row(), newCol);
            if (sidePosition.equals(lastMove.getEndPosition()) && Math.abs(lastMove.getStartPosition().row() - lastMove.getEndPosition().row()) == 2) {
                int direction = (currentTeamTurn == ChessGame.TeamColor.WHITE) ? 1 : -1;
                ChessPosition capturePos = new ChessPositionImpl(startPosition.row() + direction, newCol);

                // If the capture position is empty, add the En Passant move
                ChessPosition behindCapturePos = new ChessPositionImpl(startPosition.row(), newCol);
                if (board.getPiece(capturePos) == null && lastMove.getEndPosition().equals(behindCapturePos)) {
                    System.out.println("board.getPiece(capturePos) = " + board.getPiece(capturePos));
                    enPassantMoves.add(new ChessMoveImpl(startPosition, capturePos, null));
                    System.out.println("Added En Passant move: " + new ChessMoveImpl(startPosition, capturePos, null));
                }
            }
        }

        return enPassantMoves;
    }

    private boolean doesMoveResultInCheck(ChessMove move, ChessPiece piece) {
        // Remember the piece on the target position
        ChessPiece originalEndPiece = board.getPiece(move.getEndPosition());
        ChessPiece capturedPawn = null; // This will store the pawn that's captured in En Passant

        // Temporarily apply the move
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());

        // Check if it's an En Passant move
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                Math.abs(move.getEndPosition().column() - move.getStartPosition().column()) == 1 &&
                board.getPiece(move.getEndPosition()) == null) {
            int direction = (currentTeamTurn == TeamColor.WHITE) ? -1 : 1;
            ChessPosition capturedPawnPos = new ChessPositionImpl(move.getStartPosition().row() + direction, move.getEndPosition().column());

            capturedPawn = board.getPiece(capturedPawnPos);  // Store the captured pawn
            board.removePiece(capturedPawnPos);  // Remove the pawn from the board
        }

        // If we're moving the king, update the king's position
        ChessPosition kingPosition = (piece.getPieceType() == ChessPiece.PieceType.KING) ? move.getEndPosition() : findCurrentKingsPosition(piece.teamColor());

        // Check if the current team's king is under attack after the move
        boolean isCheck = isSquareUnderAttack(kingPosition, piece.teamColor());

        // Revert the move
        board.addPiece(move.getStartPosition(), piece);
        if (originalEndPiece != null) board.addPiece(move.getEndPosition(), originalEndPiece);
        else board.removePiece(move.getEndPosition());

        // If it was an En Passant move, restore the captured pawn
        if (capturedPawn != null) {
            int direction = (currentTeamTurn == TeamColor.WHITE) ? -1 : 1;
            ChessPosition capturedPawnPos = new ChessPositionImpl(move.getStartPosition().row() + direction, move.getEndPosition().column());
            board.addPiece(capturedPawnPos, capturedPawn);
        }

        return isCheck;
    }

    private ChessPosition findCurrentKingsPosition(TeamColor teamColor) {
        for (ChessPosition position : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.teamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING)
                return position;
        }
        return null;
    }

    private void executeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        piece.markAsMoved();

        // If it's an En Passant move, remove the pawn being captured
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getEndPosition() != null &&
                Math.abs(move.getEndPosition().column() - move.getStartPosition().column()) == 1 &&
                board.getPiece(move.getEndPosition()) == null) {
            int direction = (currentTeamTurn == TeamColor.WHITE) ? -1 : 1;
            board.removePiece(new ChessPositionImpl(move.getStartPosition().row() + direction, move.getEndPosition().column()));
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int colDiff = move.getEndPosition().column() - move.getStartPosition().column();
            if (Math.abs(colDiff) == 2) {
                ChessPosition rookOriginalPosition = (colDiff == 2) ?
                        new ChessPositionImpl(move.getStartPosition().row(), 8) :
                        new ChessPositionImpl(move.getStartPosition().row(), 1);

                ChessPosition rookNewPosition = (colDiff == 2) ?
                        new ChessPositionImpl(move.getStartPosition().row(), 6) :
                        new ChessPositionImpl(move.getStartPosition().row(), 4);

                ChessPiece rook = board.getPiece(rookOriginalPosition);
                board.removePiece(rookOriginalPosition);
                board.addPiece(rookNewPosition, rook);
                rook.markAsMoved();
            }
        }

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
        // For possibly purposeful out of turn movement after manual board setup
        if (board.getTestingMode())
            currentTeamTurn = board.getPiece(move.getStartPosition()).teamColor();

        if (validMoves(move.getStartPosition()).contains(move)) {
            // Store the last move
            board.setLastMove(move);

            executeMove(move);

            // For regular gameplay after full board reset/setup
            if (!board.getTestingMode())
                switchTeam();
        } else throw new InvalidMoveException("Invalid move.");
    }

    private void switchTeam() {
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        return isSquareUnderAttack(findCurrentKingsPosition(teamColor), teamColor);
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor != currentTeamTurn) return false;
        return ChessPositionImpl.getAllPositions().stream().noneMatch(pos -> {
            ChessPiece piece = board.getPiece(pos);
            return piece != null && piece.teamColor() == teamColor && !validMoves(pos).isEmpty();
        });
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
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