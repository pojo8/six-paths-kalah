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

}
