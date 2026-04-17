package blindchess.notation;


import chess.gameplay.ChessGameImpl;
import chess.gameplay.ChessMove;
import chess.gameplay.InvalidMoveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ChessNotationTest
{
	@Test
	void parsesSimpleOpeningMove() {
		ChessGameImpl game = new ChessGameImpl();
		game.getBoard().resetBoard();

		ChessMove move = ChessNotation.parse("e4", game);

		assertEquals("e2e4", ChessNotation.toCoordinateString(move));
		assertEquals("e4", ChessNotation.toSan(game, move));
	}

	@Test
	void parsesCastlingNotation() throws InvalidMoveException {
		ChessGameImpl game = new ChessGameImpl();
		game.getBoard().resetBoard();

		play(game, "e4");
		play(game, "e5");
		play(game, "Nf3");
		play(game, "Nc6");
		play(game, "Bc4");
		play(game, "Bc5");

		ChessMove castle = ChessNotation.parse("O-O", game);

		assertEquals("e1g1", ChessNotation.toCoordinateString(castle));
		assertEquals("O-O", ChessNotation.toSan(game, castle));
	}

	private void play(ChessGameImpl game, String notation) throws InvalidMoveException {
		game.makeMove(ChessNotation.parse(notation, game));
	}
}
