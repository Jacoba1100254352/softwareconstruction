package chess;

import java.util.ArrayList;
import java.util.List;

public record ChessPositionImpl(int row, int column) implements ChessPosition {

    public ChessPositionImpl {
        if (row < 1 || row > 8 || column < 1 || column > 8) {
            throw new IllegalArgumentException("Row and column values must be between 1 and 8.");
        }
    }

    // Utility method to generate all possible positions on a chessboard.
    public static List<ChessPosition> getAllPositions() {
        List<ChessPosition> allPositions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                allPositions.add(new ChessPositionImpl(row, col));
            }
        }
        return allPositions;
    }
}
