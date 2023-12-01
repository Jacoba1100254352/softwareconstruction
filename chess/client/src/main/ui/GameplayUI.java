package ui;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.*;

public class GameplayUI {

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

    private String[][] initializeChessboard() {
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
    }

    private void reverseBoard(String[][] board) {
        for (int i = 0; i < board.length / 2; i++) {
            String[] temp = board[i];
            board[i] = board[board.length - 1 - i];
            board[board.length - 1 - i] = temp;
        }
    }


    ///   WebSocket Functions   ///

    public void handleWebSocketMessage(String message) {
        Gson gson = new Gson();
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                updateGameState(loadGameMessage.getLoadGameMessage());
                break;
            case ERROR:
                ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                displayError(errorMessage.getErrorMessage());
                break;
            case NOTIFICATION:
                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                showNotification(notificationMessage.getNotificationMessage());
                break;
            default:
                System.out.println("Unknown message type received");
                break;
        }
    }

    public void sendGameCommand(String command) {
        // Assuming you have a WebSocketClient instance in your GameplayUI
        // myWebSocketClient.sendMessage(command);
    }

    public void updateGameState(String gameState) {
        // TODO: Implement logic to update the game state based on the gameState string
    }

    public void displayError(String errorMessage) {
        // TODO: Implement logic to display error messages to the user
    }

    public void showNotification(String notificationMessage) {
        // TODO: Implement logic to show notifications to the user
    }
}
