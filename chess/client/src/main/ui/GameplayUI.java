package ui;

import chess.*;
import WebSocketFacade.WebSocketFacade;
import clients.ChessClient;
import testFactory.TestFactory;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private static final Logger LOGGER = Logger.getLogger(GameplayUI.class.getName());
    private final WebSocketFacade webSocketFacade;

    // Constructor
    public GameplayUI(ChessClient chessClient) {
        this.webSocketFacade = new WebSocketFacade(chessClient);

        connectToGameServer();
    }

    ///   Gameplay Functions   ///

    public void drawChessboard() {
        System.out.println("Initial Chessboard State:");

        // Drawing chessboard with white pieces at bottom
        System.out.println("White at bottom:");
        drawBoard(true);

        // Drawing chessboard with black pieces at bottom
        System.out.println("Black at bottom:");
        drawBoard(false);
    }

    private void drawBoard(boolean whiteAtBottom) {
        String[][] board = initializeChessboard();

        if (!whiteAtBottom)
            reverseBoard(board);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++)
                System.out.print(board[i][j]);

            System.out.println();
        }
    }

    /*private String[][] initializeChessboard() {
        String[][] board = new String[8][8];

        String[] blackPieces = {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,
                EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP,
                EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK};
        String[] whitePieces = {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP,
                EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP,
                EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK};

        for (int i = 0; i < 8; i++) {
            board[0][i] = blackPieces[i];
            board[1][i] = EscapeSequences.BLACK_PAWN;
            board[6][i] = EscapeSequences.WHITE_PAWN;
            board[7][i] = whitePieces[i];

            for (int j = 2; j < 6; j++)
                board[j][i] = EscapeSequences.EMPTY;
        }

        return board;
    }*/

    private void reverseBoard(String[][] board) {
        for (int i = 0; i < board.length / 2; i++) {
            String[] temp = board[i];
            board[i] = board[board.length - 1 - i];
            board[board.length - 1 - i] = temp;
        }
    }


    ///   WebSocket Functions   ///

    public void connectToGameServer() {
        try {
            webSocketFacade.connect("ws://localhost:" + TestFactory.getServerPort() + "/connect");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to game server", e);
        }
    }

    public void displayError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
        // Add any additional UI handling for errors here
    }

    public void showNotification(String notificationMessage) {
        System.out.println("Notification: " + notificationMessage);
        // Add any additional UI handling for notifications here
    }

    // Method to redraw the chessboard
    public void redraw(ChessGame game, ArrayList<ChessPosition> highlights, ChessPosition pieceToHighlight) {
        String[][] board = initializeChessboard();
        updateBoardWithPieces(board, game);
        printBoard(board, highlights, pieceToHighlight);
    }

    // Initialize an empty chessboard
    private String[][] initializeChessboard() {
        String[][] board = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = "   "; // Empty space
            }
        }
        return board;
    }

    // Update the board with pieces from the game state
    private void updateBoardWithPieces(String[][] board, ChessGame game) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPositionImpl position = new ChessPositionImpl(row, col);
                ChessPiece piece = game.getBoard().getPiece(position);
                if (piece != null) {
                    board[row - 1][col - 1] = piece.getPieceType().name(); // Replace 'getSymbol' with your method to get piece representation
                }
            }
        }
    }

    // Print the chessboard to the console
    private void printBoard(String[][] board, ArrayList<ChessPosition> highlights, ChessPosition pieceToHighlight) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String cell = board[i][j];
                if (highlights != null && highlights.contains(new ChessPositionImpl(i + 1, j + 1))) {
                    cell = "[" + cell + "]"; // Highlighted cell
                }
                if (pieceToHighlight != null && pieceToHighlight.equals(new ChessPositionImpl(i + 1, j + 1))) {
                    cell = "{" + cell + "}"; // Specifically highlighted piece
                }
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public void displayBoard(ChessBoard board, String color, ArrayList<ChessPosition> highlights, ChessPosition pieceToHighlight) {
        String rowLabels, colLabels;
        int startRow, endRow, stepRow;

        if (color.equals("white")) {
            rowLabels = "    a  b  c  d  e  f  g  h    ";
            colLabels = "  8 7 6 5 4 3 2 1 ";
            startRow = 8; endRow = 0; stepRow = -1;
        } else {
            rowLabels = "    h  g  f  e  d  c  b  a    ";
            colLabels = "  1 2 3 4 5 6 7 8 ";
            startRow = 1; endRow = 9; stepRow = 1;
        }

        System.out.println(rowLabels);
        for (int row = startRow; row != endRow; row += stepRow) {
            System.out.print((row % 8 + 1) + " ");
            for (int col = 1; col <= 8; col++) {
                ChessPosition curPos = new ChessPositionImpl(row, col);
                String bgColor = ((row + col) % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;

                if (highlights != null && highlights.contains(curPos)) {
                    bgColor = ((row + col) % 2 == 0) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                }
                if (pieceToHighlight != null && pieceToHighlight.equals(curPos)) {
                    bgColor = SET_BG_COLOR_YELLOW;
                }

                String pieceStr = "   ";
                ChessPiece piece = board.getPiece(curPos);
                if (piece != null) {
                    String pieceColor = (piece.teamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                    pieceStr = " " + piece.toString().toUpperCase() + " ";
                    System.out.print(pieceColor);
                }
                System.out.print(bgColor + pieceStr);
            }
            System.out.println((row % 8 + 1));
        }
        System.out.println(colLabels);
    }

}
