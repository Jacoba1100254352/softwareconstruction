package ui;

import chess.*;
import WebSocketFacade.WebSocketFacade;
import clients.ChessClient;
import com.google.gson.Gson;
import testFactory.TestFactory;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.NotificationMessage;

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
            LOGGER.log(Level.INFO, "Connected to Server");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to game server", e);
        }
    }

    public void displayError(String errorMessage) {
        System.out.println(new Gson().fromJson(errorMessage, ErrorMessage.class).getErrorMessage());
    }

    public void showNotification(String notificationMessage) {
        System.out.println(new Gson().fromJson(notificationMessage, NotificationMessage.class).getNotificationMessage());
    }

    // Method to redraw the chessboard
    public void redraw(ChessGame game, ArrayList<ChessPosition> highlights, ChessPosition pieceToHighlight) {
        String[][] board = initializeChessboard();
        updateBoardWithPieces(board, game);
        printBoard(board, highlights, pieceToHighlight);

        /*
                    System.out.println("Game updated.");
            LoadMessage loadMessage = new Gson().fromJson(message, LoadMessage.class);
            String gamestr = loadMessage.getGame();

            var builder = new GsonBuilder();
            builder.registerTypeAdapter(ChessBoard.class, new BoardAdapter());
            builder.registerTypeAdapter(ChessPiece.class, new PieceAdapter());

            game = builder.create().fromJson(gamestr, GameImpl.class);
            System.out.println();
            displayBoard(game.getBoard());
            System.out.printf("%s >>> ", loggedIn ? "WELCOME" : "LOGIN");
         */
    }

    // Initialize an empty chessboard
    private String[][] initializeChessboard() {
        String[][] board = new String[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = "   ";
        return board;
    }

    // Update the board with pieces from the game state
    private void updateBoardWithPieces(String[][] board, ChessGame game) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = game.getBoard().getPiece(new ChessPositionImpl(row, col));
                if (piece != null) {
                    board[row - 1][col - 1] = piece.getPieceType().name();
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
