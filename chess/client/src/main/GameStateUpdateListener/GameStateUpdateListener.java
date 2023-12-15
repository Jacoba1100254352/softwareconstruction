package GameStateUpdateListener;

import chess.ChessGame;

public interface GameStateUpdateListener {
    void onGameStateUpdate(ChessGame updatedGame);
}
