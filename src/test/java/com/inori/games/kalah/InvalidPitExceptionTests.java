package com.inori.games.kalah;

import com.inori.games.kalah.exceptions.InvalidPitException;
import com.inori.games.kalah.util.KalahUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@RunWith(JUnit4.class)
public class InvalidPitExceptionTests {


    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @DisplayName("Testing exception is thrown for player 1 selecting their Kalah")
    public void InvalidKalahSelectionPlayer1(){
        expectedException.expect(InvalidPitException.class);
        String expectedExceptionMessage = "The pit selected cannot be a Kalah with pit id 7 or 14";
        try {
            KalahUtils.moveMadeBy(7);
        } catch( InvalidPitException ipe){
            assertEquals(expectedExceptionMessage, ipe.getMessage());
        }
    }

    @Test
    @DisplayName("Testing exception is thrown for player 2 selecting their Kalah")
    public void InvalidKalahSelectionPlayer2(){
        expectedException.expect(InvalidPitException.class);
        String expectedExceptionMessage = "The pit selected cannot be a Kalah with pit id 7 or 14";
        try {
            KalahUtils.moveMadeBy(14);
        } catch( InvalidPitException ipe){
            assertEquals(expectedExceptionMessage, ipe.getMessage());
        }
    }

    @Test
    @DisplayName("Testing exception is thrown for a player selecting an invalid pit")
    public void InvalidPitSelection(){
        expectedException.expect(InvalidPitException.class);
        String expectedExceptionMessage = "The pit selected must be either 1 - 6 for player 1 or 8 - 13 for player 2";
        try {
            KalahUtils.moveMadeBy(42);
        } catch( InvalidPitException ipe){
            assertEquals(expectedExceptionMessage, ipe.getMessage());
        }
    }


}
