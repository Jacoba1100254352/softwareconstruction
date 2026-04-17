package blindchess.bot;


import chess.gameplay.ChessGame;
import chess.gameplay.ChessMove;


public interface ChessBot
{
	ChessMove chooseMove(ChessGame game, ChessGame.TeamColor teamColor);
}
