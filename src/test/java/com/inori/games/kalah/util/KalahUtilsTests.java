package com.inori.games.kalah.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

import static com.inori.games.kalah.common.AppConstants.PLAYER1;
import static com.inori.games.kalah.common.AppConstants.PLAYER2;

class KalahUtilsTests {

	final static String jsonState = "{\"1\" : 6, \"2\" : 6, \"3\" : 6, \"4\" : 6, \"5\" : 6, \"6\" : 6, \"7\" : 0, \"8\" : 6, \"9\" : 6, \"10\" : 6, \"11\" : 6, \"12\" : 6, \"13\" : 6, \"14\" : 0}";
	final static String p1WinsState = "{\"1\" : 0, \"2\" : 0, \"3\" : 0, \"4\" : 0, \"5\" : 0, \"6\" : 0, \"7\" : 36, \"8\" : 6, \"9\" : 6, \"10\" : 6, \"11\" : 6, \"12\" : 6, \"13\" : 6, \"14\" : 0}";
	final static String p2WinsState = "{\"8\" : 0, \"9\" : 0, \"10\" : 0, \"11\" : 0, \"12\" : 0, \"13\" : 0, \"14\" : 36, \"1\" : 6, \"2\" : 6, \"3\" : 6, \"4\" : 6, \"5\" : 6, \"6\" : 6, \"7\" : 0}";



	@Test
	@DisplayName("Test case in selecting a players first move")
	void playerSelectionTest() {
		String firstPlayer = KalahUtils.selectFirstMove();
		assertTrue(firstPlayer.equals(PLAYER1) || firstPlayer.equals(PLAYER2));
	}

	@Test
	@DisplayName("Testing conversion form json string to HashMap")
	void jsonConversionTest(){
		Object convertedState = KalahUtils.convertJsonToHmap(jsonState);

		assertEquals("java.util.HashMap", convertedState.getClass().getName());
	}

	@Test
	@DisplayName("Testing player 1 wins")
	void WinningPlayer1Test(){
		HashMap<String,Object> convertedState = KalahUtils.convertJsonToHmap(p1WinsState);
		String winner = KalahUtils.hasGameCompleted(convertedState);

		assertEquals("Player 1", winner);
	}

	@Test
	@DisplayName("Testing player 2 wins")
	void WinningPlayer2Test(){
		HashMap<String,Object> convertedState = KalahUtils.convertJsonToHmap(p2WinsState);
		String winner = KalahUtils.hasGameCompleted(convertedState);

		assertEquals("Player 2", winner);
	}

}
