package blindchess.model;


import blindchess.bot.MinimaxChessBot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BlindChessSessionTest
{
	@Test
	void historyModeRetainsFormattedTranscript() {
		BlindChessSession session = new BlindChessSession(
				"session-history",
				GameMode.HISTORY,
				chess.gameplay.ChessGame.TeamColor.WHITE,
				new MinimaxChessBot(1)
		);

		BlindChessSession.TurnOutcome outcome = session.playPlayerMove("e4");

		assertEquals("e4", outcome.playerMove());
		assertNotNull(outcome.botMove());
		assertFalse(session.getVisibleHistory().isEmpty());
		assertTrue(session.getVisibleHistory().get(0).startsWith("1. e4"));
	}

	@Test
	void noHistoryModeOnlyShowsLatestMove() {
		BlindChessSession session = new BlindChessSession(
				"session-no-history",
				GameMode.NO_HISTORY,
				chess.gameplay.ChessGame.TeamColor.WHITE,
				new MinimaxChessBot(1)
		);

		BlindChessSession.TurnOutcome outcome = session.playPlayerMove("e4");

		assertEquals(1, session.getVisibleHistory().size());
		assertEquals(session.getLatestMove(), session.getVisibleHistory().get(0));
		assertEquals(outcome.botMove(), session.getLatestMove());
	}

	@Test
	void blackPlayerReceivesBotOpeningMove() {
		BlindChessSession session = new BlindChessSession(
				"session-black",
				GameMode.NO_HISTORY,
				chess.gameplay.ChessGame.TeamColor.BLACK,
				new MinimaxChessBot(1)
		);

		assertNotNull(session.getLatestMove());
		assertTrue(session.isPlayerTurn());
	}
}
