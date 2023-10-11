package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
     * Checks if a given square on the board is under attack by opponent's pieces.
     */
    private boolean isSquareUnderAttack(ChessBoard board, ChessPosition position, TeamColor teamColor) {
        // Determine the enemy's color
        ChessGame.TeamColor enemyColor = (teamColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        // Iterate through all board positions to find attacking pieces
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece != null && piece.teamColor() == enemyColor) {
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);

                // If the piece is a king, filter out castling moves
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    moves = moves.stream()
                            .filter(move -> Math.abs(move.getEndPosition().column() - move.getStartPosition().column()) <= 1)
                            .collect(Collectors.toList());
                }

                if (moves.stream().anyMatch(move -> move.getEndPosition().equals(position)))
                    return true;  // Found an attacking move on the position
            }
        }
        return false;  // No attacks found on the position
    }


    // In KingPiece class
    private boolean rookHasMoved(ChessBoard board, ChessPosition kingPosition, boolean kingSide) {
        ChessPosition rookPosition = kingSide ?
                new ChessPositionImpl(kingPosition.row(), 8) :  // King-side rook
                new ChessPositionImpl(kingPosition.row(), 1);  // Queen-side rook

        ChessPiece rook = board.getPiece(rookPosition);
        return rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved();
    }

    /**
     * Checks if king-side castling is possible from a given position.
     */
    private boolean canCastleKingSide(ChessBoard board, ChessPosition position, TeamColor color) {
        ChessPiece king = board.getPiece(findKingPosition(color));
        if (king == null) {
            System.out.println("King could not be found, color = " + color);
            return false;
        } else if (king.getPieceType() != ChessPiece.PieceType.KING || king.teamColor() != color) {
            System.out.println("Something funky happened, Piece Type = " + king.getPieceType() + ", color = " + color);
            return false;
        }

        // Preliminary check: If king is close to the edge, castling is not possible
        if (position.column() >= 7 || king.hasMoved()) return false;

        // Check if the path for castling is clear and safe
        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.row(), 8));
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK) return false;

        // Check if rook has moved
        if (rookHasMoved(board, position, true)) return false;

        for (int i = 1; i <= 2; i++)
            if (board.getPiece(new ChessPositionImpl(position.row(), position.column() + i)) != null || isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() + i), color))
                return false;
        return true;
    }



    /**
     * Checks if queen-side castling is possible from a given position.
     */
    private boolean canCastleQueenSide(ChessBoard board, ChessPosition position, TeamColor color) {
        ChessPiece king = board.getPiece(findKingPosition(color));
        if (king == null) {
            System.out.println("King could not be found, color = " + color);
            return false;
        } else if (king.getPieceType() != ChessPiece.PieceType.KING || king.teamColor() != color) {
            System.out.println("Something funky happened, Piece Type = " + king.getPieceType() + ", color = " + color);
            return false;
        }

        // Preliminary check: If king is close to the edge, castling is not possible
        if (position.column() >= 7 || king.hasMoved()) return false;

        // Check if the path for castling is clear and safe
        ChessPiece rook = board.getPiece(new ChessPositionImpl(position.row(), 1));
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK) return false;

        // Check if rook has moved
        if (rookHasMoved(board, position, false)) return false;

        return IntStream.range(1, 4)
                .allMatch(i -> board.getPiece(new ChessPositionImpl(position.row(), position.column() - i)) == null
                        && !isSquareUnderAttack(board, new ChessPositionImpl(position.row(), position.column() - i), color));

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

        // Add Castling Moves if the piece is a King
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (canCastleKingSide(board, startPosition, piece.teamColor()))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() + 2), null));
            if (canCastleQueenSide(board, startPosition, piece.teamColor()))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() - 2), null));
        }

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
        return null;
    }

    private boolean isPositionUnderAttack(ChessPosition position, TeamColor defendingTeam) {
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece attackingPiece = board.getPiece(pos);
            // Only consider pieces from the opposing team as threats
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
        piece.markAsMoved();
        board.removePiece(move.getStartPosition());

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

        return ChessPositionImpl.getAllPositions().stream().noneMatch(pos -> {
            ChessPiece piece = board.getPiece(pos);
            return piece != null && piece.teamColor() == teamColor && !validMoves(pos).isEmpty();
        });
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor != currentTeamTurn || isInCheck(teamColor)) return false;

        return ChessPositionImpl.getAllPositions().stream().noneMatch(pos -> {
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