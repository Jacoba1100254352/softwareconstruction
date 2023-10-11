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

    private boolean canCastle(ChessBoard board, ChessPosition position, TeamColor color, boolean kingSide) {
        ChessPiece king = board.getPiece(findKingPosition(color));
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
        if (piece == null || piece.teamColor() != currentTeamTurn) return new ArrayList<>();

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        ChessPosition kingPosition = findKingPosition(currentTeamTurn);
        Collection<ChessMove> validMoves = moves.stream()
                .filter(move -> !doesMoveResultInCheck(move, kingPosition, piece))
                .collect(Collectors.toList());

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (canCastle(board, startPosition, piece.teamColor(), true))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() + 2), null));
            if (canCastle(board, startPosition, piece.teamColor(), false))
                validMoves.add(new ChessMoveImpl(startPosition, new ChessPositionImpl(startPosition.row(), startPosition.column() - 2), null));
        }

        return validMoves;
    }

    private boolean doesMoveResultInCheck(ChessMove move, ChessPosition kingPosition, ChessPiece piece) {
        ChessPiece originalEndPiece = board.getPiece(move.getEndPosition()); // Save the piece at the end position
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());

        if (piece.getPieceType() == ChessPiece.PieceType.KING) kingPosition = move.getEndPosition();

        boolean isCheck = isSquareUnderAttack(kingPosition, currentTeamTurn);

        board.addPiece(move.getStartPosition(), piece);
        if (originalEndPiece != null) board.addPiece(move.getEndPosition(), originalEndPiece); // Restore original piece
        else board.removePiece(move.getEndPosition());

        return isCheck;
    }

    // Fields to store the position of the kings to optimize the findKingPosition method
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    private ChessPosition findKingPosition(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return whiteKingPosition;
        } else {
            return blackKingPosition;
        }
    }

    private void updateKingPosition(ChessPosition position, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.teamColor() == TeamColor.WHITE) {
                whiteKingPosition = position;
            } else {
                blackKingPosition = position;
            }
        }
    }

    private boolean isSquareUnderAttack(ChessPosition position, TeamColor teamColor) {
        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        if (position == null)
            return false;

        // Directly check for pawns
        if (teamColor == TeamColor.WHITE) {
            if (position.row() > 1 && position.column() > 1 && position.column() < 8) {
                if (board.getPiece(new ChessPositionImpl(position.row() - 1, position.column() - 1)) instanceof PawnPiece
                        || board.getPiece(new ChessPositionImpl(position.row() - 1, position.column() + 1)) instanceof PawnPiece) {
                    return true;
                }
            }
        } else {
            if (position.row() < 8 && position.column() > 1 && position.column() < 8) {
                if (board.getPiece(new ChessPositionImpl(position.row() + 1, position.column() - 1)) instanceof PawnPiece
                        || board.getPiece(new ChessPositionImpl(position.row() + 1, position.column() + 1)) instanceof PawnPiece) {
                    return true;
                }
            }
        }

        // Check other pieces in a more optimized manner
        for (ChessPosition pos : ChessPositionImpl.getAllPositions()) {
            ChessPiece piece = board.getPiece(pos);
            if (piece != null && piece.teamColor() == enemyColor) {
                if (piece.canAttack(board, pos, position)) {
                    return true;
                }
            }
        }

        return false;
    }


    private void executeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        piece.markAsMoved();
        board.removePiece(move.getStartPosition());

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            updateKingPosition(move.getEndPosition(), piece);
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

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()).contains(move)) {
            executeMove(move);
            switchTeam();
        } else throw new InvalidMoveException("Invalid move.");
    }

    private void switchTeam() {
        currentTeamTurn = (currentTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        return isSquareUnderAttack(findKingPosition(teamColor), teamColor);
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
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }
}