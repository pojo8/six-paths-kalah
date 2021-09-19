package com.inori.games.kalah.component;


import com.inori.games.kalah.model.GameTurn;
import com.inori.games.kalah.repository.GameTurnRepository;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class MoveComponentTests {

    @Spy
    private GameTurnRepository gameTurnRepository;

    @InjectMocks
    private MoveComponent moveComponent;

    final String newGameState = "{\"1\" : 6, \"2\" : 6, \"3\" : 6, \"4\" : 6, \"5\" : 6, \"6\" : 6, \"7\" : 0, \"8\" : 6, \"9\" : 6, \"10\" : 6, \"11\" : 6, \"12\" : 6, \"13\" : 6, \"14\" : 0}";
    final String p1CaptureState = "{\"8\" : 7, \"9\" : 0, \"10\" : 7, \"11\" : 7, \"12\" : 7, \"13\" : 7, \"14\" : 1, \"1\" : 6, \"2\" : 6, \"3\" : 6, \"4\" : 6, \"5\" : 6, \"6\" : 6, \"7\" : 0}";
    final String p2CaptureState = "{\"1\" : 7, \"2\" : 0, \"3\" : 7, \"4\" : 7, \"5\" : 7, \"6\" : 7, \"7\" : 1, \"8\" : 6, \"9\" : 6, \"10\" : 6, \"11\" : 6, \"12\" : 6, \"13\" : 6, \"14\" : 0}";

    @Before
    public void init(){
        GameTurn turn =  new GameTurn(1,1000, 1 );
        doNothing().when(gameTurnRepository).updateGameMove(anyInt(),anyInt());
    }


    @Test
    @DisplayName("Testing first player moving to pit 2")
    public void player1MoveOnPit2(){
        doNothing().when(gameTurnRepository).updateGameMove(anyInt(), anyInt());
        HashMap<String, Object> stateMap = moveComponent.gameMove(newGameState, "2","1000", 1);
        assertEquals(0,stateMap.get("2"));
        assertEquals(7,stateMap.get("1"));
        assertEquals(7,stateMap.get("3"));
        assertEquals(7,stateMap.get("4"));
        assertEquals(7,stateMap.get("5"));
        assertEquals(7,stateMap.get("6"));
        assertEquals(1,stateMap.get("7"));
    }

    @Test
    @DisplayName("Testing second player moving to pit 8")
    public void player2MoveOnPit8(){
        doNothing().when(gameTurnRepository).updateGameMove(anyInt(), anyInt());
        HashMap<String, Object> stateMap = moveComponent.gameMove(newGameState, "8","1000", 2);
        assertEquals(0,stateMap.get("8"));
        assertEquals(7,stateMap.get("9"));
        assertEquals(7,stateMap.get("10"));
        assertEquals(7,stateMap.get("11"));
        assertEquals(7,stateMap.get("12"));
        assertEquals(7,stateMap.get("13"));
        assertEquals(1,stateMap.get("14"));
    }

    @Test
    @DisplayName("Testing first player capturing move to pit 1")
    public void player1CaptureMoveOnPit1(){
        doNothing().when(gameTurnRepository).updateGameMove(anyInt(), anyInt());
        HashMap<String, Object> stateMap = moveComponent.gameMove(p1CaptureState, "1","1000", 1);
        assertEquals(0,stateMap.get("1"));
        assertEquals(7,stateMap.get("2"));
        assertEquals(7,stateMap.get("3"));
        assertEquals(7,stateMap.get("4"));
        assertEquals(7,stateMap.get("5"));
        assertEquals(7,stateMap.get("6"));
        assertEquals(2,stateMap.get("7"));
        assertEquals(0,stateMap.get("14"));
    }

    @Test
    @DisplayName("Testing second player captuiring move to pit 8")
    public void player2CaptureMoveOnPit8(){
        doNothing().when(gameTurnRepository).updateGameMove(anyInt(), anyInt());
        HashMap<String, Object> stateMap = moveComponent.gameMove(p2CaptureState, "8","1000", 2);
        assertEquals(0,stateMap.get("8"));
        assertEquals(7,stateMap.get("9"));
        assertEquals(7,stateMap.get("10"));
        assertEquals(7,stateMap.get("11"));
        assertEquals(7,stateMap.get("12"));
        assertEquals(7,stateMap.get("13"));
        assertEquals(2,stateMap.get("14"));
        assertEquals(0,stateMap.get("7"));
    }
}
