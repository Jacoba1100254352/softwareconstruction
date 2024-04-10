package GameStateUpdateListener;


import chess.gameplay.ChessGame;


public interface GameStateUpdateListener
{
	void onGameStateUpdate(ChessGame updatedGame);
}
