package chess.gameplay;


import chess.pieces.ChessPiece;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this interface, but you should not alter the existing
 * methods.
 */
public interface ChessBoard
{
	
	/**
	 * Adds a chess piece to the chessboard
	 *
	 * @param position where to add the piece to
	 * @param piece    the piece to add
	 */
	void addPiece(ChessPosition position, ChessPiece piece);
	
	/**
	 * Gets a chess piece on the chessboard
	 *
	 * @param position The position to get the piece from
	 *
	 * @return Either the piece at the position, or null if no piece is at that
	 * position
	 */
	ChessPiece getPiece(ChessPosition position);
	
	/**
	 * Sets the board to the default starting board
	 * (How the game of chess normally starts)
	 */
	void resetBoard();
	
	/**
	 * Removes a chess piece from the chessboard.
	 *
	 * @param position where to remove the piece from
	 */
	void removePiece(ChessPosition position);
	
	
	/**
	 * @return lastMove that was made in the game
	 */
	ChessMove getLastMove();
	
	/**
	 * Save the last move made in the game
	 *
	 * @param lastMove sets the private variable lastMove
	 */
	void setLastMove(ChessMove lastMove);
	
	/**
	 * Private variable for managing test cases depending on manual board setup or default setup
	 *
	 * @return testingMode for toggling turn-based game play
	 */
	boolean getTestingMode();
}
