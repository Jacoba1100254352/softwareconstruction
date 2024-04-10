package ui;


import chess.gameplay.ChessBoard;
import chess.gameplay.ChessGame;
import chess.gameplay.ChessPosition;
import chess.gameplay.ChessPositionImpl;
import chess.pieces.ChessPiece;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.NotificationMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ui.EscapeSequences.*;


public class GameplayUI
{
	private static final Logger LOGGER = Logger.getLogger(GameplayUI.class.getName());
	
	static {
		LOGGER.setLevel(Level.WARNING);
	}
	
	///   WebSocket Functions   ///
	
	// Method for highlighting the board
	private static void highlightBoard(ChessBoard board, Collection<ChessPosition> highlights, String[][] boardArr) {
		for (ChessPosition highlight : highlights) {
			int row = highlight.getRow() - 1;
			int col = highlight.getCol() - 1;
			String pieceString = boardArr[row][col];
			boardArr[row][col] = "X" + pieceString; // Highlight the cell
		}
	}
	
	// Convert a row array to a string for display
	private static String rowtoString(String[] row, int rownum, boolean blackStart, boolean reverse) {
		StringBuilder rowString = new StringBuilder(SET_BG_COLOR_DARK_GREEN + " " + rownum + " " + (blackStart ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY));
		boolean black = !blackStart;
		for (int i = reverse ? 7 : 0; reverse ? i >= 0 : i < 8; i += reverse ? -1 : 1) {
			String piece = row[i];
			if (piece.startsWith("X")) {
				piece = piece.substring(1);
				rowString.append(black ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN);
			}
			rowString.append(piece.equals(" ") ? "   " : piece);
			rowString.append(black ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY);
			black = !black;
		}
		return rowString + SET_BG_COLOR_DARK_GREEN + " " + rownum + " " + RESET_BG_COLOR;
	}
	
	// Print the entire board
	private static void printBoard(String[][] board, boolean reverse) {
		// Label for columns
		String labels = reverse ? "    h  g  f  e  d  c  b  a    " : "    a  b  c  d  e  f  g  h    ";
		
		// Print column labels
		System.out.println(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + labels + RESET_BG_COLOR);
		
		// Print each row of the board
		for (int i = reverse ? 0 : 7; reverse ? i < 8 : i >= 0; i += reverse ? 1 : -1) {
			System.out.println(rowtoString(board[i], i + 1, i % 2 != (reverse ? 1 : 0), reverse));
		}
		
		// Print column labels again at the bottom
		System.out.println(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + labels + RESET_BG_COLOR);
	}
	
	// Get string representation of a chess piece
	private static String getPieceString(ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
		// Use constants from EscapeSequences class
		return switch (pieceType) {
			case KING -> teamColor == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
			case QUEEN -> teamColor == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
			case BISHOP -> teamColor == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
			case KNIGHT -> teamColor == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
			case ROOK -> teamColor == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
			case PAWN -> teamColor == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
			default -> EMPTY;
		};
	}
	
	public void displayError(String errorMessage) {
		System.out.println(new Gson().fromJson(errorMessage, ErrorMessage.class).getErrorMessage());
	}
	
	public void showNotification(String notificationMessage) {
		System.out.println(new Gson().fromJson(notificationMessage, NotificationMessage.class).getNotificationMessage());
	}
	
	// Method to redraw the chessboard
	public void redrawGame(ChessGame game, String color) {
		displayBoard(game.getBoard(), color, null, null);
	}
	
	public void displayBoard(ChessBoard board, String color, ArrayList<ChessPosition> highlights, ChessPosition pieceToHighlight) {
		String[][] boardArr = new String[8][8];
		for (int i = 1; i <= 8; i++) {
			String[] row = new String[8];
			for (int j = 1; j <= 8; j++) {
				ChessPiece piece = board.getPiece(new ChessPositionImpl(i, j));
				row[j - 1] = piece == null ? EMPTY : getPieceString(piece.getPieceType(), piece.teamColor());
			}
			boardArr[i - 1] = row;
		}
		
		// Highlighting logic (if needed)
		if (highlights != null && !highlights.isEmpty()) {
			highlightBoard(board, highlights, boardArr);
		}
		
		// Determine orientation based on player color
		boolean reverse = !color.equals("white");
		printBoard(boardArr, reverse);
	}
	
}
