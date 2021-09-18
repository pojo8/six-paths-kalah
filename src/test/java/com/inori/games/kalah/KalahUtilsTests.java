package com.inori.games.kalah;

import com.inori.games.kalah.util.KalahUtils;
import org.checkerframework.checker.fenum.qual.AwtAlphaCompositingRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static com.inori.games.kalah.common.AppConstants.PLAYER1;
import static com.inori.games.kalah.common.AppConstants.PLAYER2;

class KalahUtilsTests {

	@Test
	@DisplayName("Test case in selecting a players first move")
	void playerSelectionTest() {
		String firstPlayer = KalahUtils.selectFirstMove();
		assertTrue(firstPlayer.equals(PLAYER1) || firstPlayer.equals(PLAYER2));
	}

	@Test
	@DisplayName("Testing conversion form json string to HashMap")
	void jsonConversionTest(){
		String jsonState = "{\"1\" : 6, \"2\" : 6, \"3\" : 6, \"4\" : 6, \"5\" : 6, \"6\" : 6, \"7\" : 0, \"8\" : 6, \"9\" : 6, \"10\" : 6, \"11\" : 6, \"12\" : 6, \"13\" : 6, \"14\" : 0}";
		Object convertedState = KalahUtils.convertJsonToHmap(jsonState);

		assertEquals("java.util.HashMap", convertedState.getClass().getName());
	}

}
