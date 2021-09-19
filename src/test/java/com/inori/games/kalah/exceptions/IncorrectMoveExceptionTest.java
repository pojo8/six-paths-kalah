package com.inori.games.kalah.exceptions;

import com.inori.games.kalah.controller.KalahRestController;
import com.inori.games.kalah.exceptions.IncorrectPlayerMoveException;
import com.inori.games.kalah.model.GameTurn;
import com.inori.games.kalah.repository.GameTurnRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(MockitoExtension.class)
public class IncorrectMoveExceptionTest {

    @Mock
    private KalahRestController kalahRestController;

    @MockBean
    private GameTurnRepository gameTurnRepository;

    @Before
    public void init(){
        // Sets up our mock game turn repo and the next player is 2
        GameTurn turn =  new GameTurn(1,1000, 2 );
        when(gameTurnRepository.getNextPlayerByGameId(1000)).thenReturn(turn.getNextPlayer());
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @DisplayName("Testing prompt for a move taken out of the turn order")
    public void InvalidPlayerTurn(){
        expectedException.expect(IncorrectPlayerMoveException.class);
        String expectedExceptionMessage = "It is currently not your turn. Please allow the Player 2 to make their move";
        assertNotNull(kalahRestController);

        try{
            kalahRestController.moveStones("1000", "2");
        } catch (IncorrectPlayerMoveException ime){
            assertEquals(expectedExceptionMessage, ime.getMessage());
        }
    }
}
