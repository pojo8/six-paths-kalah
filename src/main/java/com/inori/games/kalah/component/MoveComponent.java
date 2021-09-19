package com.inori.games.kalah.component;

import com.inori.games.kalah.repository.GameTurnRepository;
import com.inori.games.kalah.util.KalahUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.inori.games.kalah.common.AppConstants.PLAYER_1_PITS;
import static com.inori.games.kalah.common.AppConstants.PLAYER_2_PITS;

@Slf4j
@Component
public class MoveComponent {

    @Autowired
    private GameTurnRepository gameTurnRepository;

    public HashMap<String, Object> gameMove(String gameState, String pitId, String gameId, Integer playersTurn) {
        AtomicInteger stonesPickedUp = new AtomicInteger(0);
        Integer gameIdValue = Integer.valueOf(gameId);
        Integer pitIdValue = Integer.valueOf(pitId);

        HashMap<String, Object> stateMap = KalahUtils.convertJsonToHmap(gameState);

        Integer stones = (Integer) stateMap.get(pitId);

        // Early exit of player selects a pit with no stones
        if(stones.equals(0)){
            log.info("This pit has no stones - it is now the other players turn");
            if (playersTurn.equals(1)) {
                gameTurnRepository.updateGameMove(2, gameIdValue);
                return stateMap;
            } else {
                gameTurnRepository.updateGameMove(1, gameIdValue);
                return stateMap;
            }
        }

        stonesPickedUp.lazySet(stones);

        // Clear stones in that pit
        stateMap.put(pitId, 0);

        if(playersTurn.equals(1)) {
            for (int i = pitIdValue + 1; i <= stones + 2; i++) {

                if (i == (stones + 2) && !(stonesPickedUp.getAcquire() == 0)) {
                    i = 0;
                } else {
                    Integer stonesInNextPit = (Integer) stateMap.get(String.valueOf(i));
                    log.info("Adding a stone to pit {}", i);

                    // next move is into an empty pit capture stones
                    if (stonesPickedUp.get() == 1 && stonesInNextPit == 0) {
                        log.info("Great move! Capturing opponents stones");
                        Integer capturedStones = 0;
                        String pitPosition = PLAYER_2_PITS.get((i-PLAYER_2_PITS.size()));
                        capturedStones = (Integer) stateMap.get(pitPosition);

                        Integer stonesInCapturePit = (Integer) stateMap.get(String.valueOf(i));
                        stateMap.put(String.valueOf(i), stonesInCapturePit + capturedStones + 1);
                        stateMap.put(pitPosition, 0);

                        gameTurnRepository.updateGameMove(2, gameIdValue);
                        stonesPickedUp.decrementAndGet();

                        log.info("Adding {} captured stones plus your last stone to pit: {}", capturedStones, i);
                        // granted last stone in kalah your move again
                    } else if (stonesPickedUp.get() == 1 && i == 7) {
                        // case of player 1
                        stateMap.put(String.valueOf(i), stonesInNextPit + 1);
                        gameTurnRepository.updateGameMove(1, gameIdValue);
                        stonesPickedUp.decrementAndGet();
                        log.info("Nice move! It is your turn again");
                    } else if (stonesPickedUp.get() == 0 && i !=7) {
                        // case of player 1
                        gameTurnRepository.updateGameMove(2, gameIdValue);
                        log.info("Next players turn");
                    } else {
                        stateMap.put(String.valueOf(i), stonesInNextPit + 1);
                        stonesPickedUp.decrementAndGet();
                    }
                }
            }

            // Splitting logic for player 2
        } else {

            for (int j = pitIdValue + 1; j <= 14 + 1; j++) {
                // gets the number in the pit

                if (j == (14 + 1) && !(stonesPickedUp.getAcquire() == 0)) {
                    j = 0;
                } else {
                    Integer stonesInNextPit = (Integer) stateMap.get(String.valueOf(j));
                    log.info("Adding a stone to pit {}", j);

                    //  Granted that next move is into an empty pit capture stones
                    if (stonesPickedUp.get() == 1 && stonesInNextPit == 0) {
                        log.info("Great move! Capturing opponents stones");
                        Integer capturedStones = 0;

                        String pitPosition = PLAYER_1_PITS.get((j-1) - (PLAYER_1_PITS.size()));

                        Integer stonesInCapturePit = (Integer) stateMap.get(String.valueOf(j));
                        stateMap.put(String.valueOf(j), stonesInCapturePit + capturedStones + 1);
                        capturedStones = (Integer) stateMap.get(pitPosition);
                        stateMap.put(String.valueOf(j), capturedStones + 1);
                        stateMap.put(pitPosition, 0);

                        gameTurnRepository.updateGameMove(1, gameIdValue);
                        stonesPickedUp.decrementAndGet();

                        log.info("Adding {} captured stones plus your last stone to pit: {}", capturedStones, j);
                      // granted last stone in kalah your move again
                    } else if (stonesPickedUp.get() == 1 && j == 14) {

                        stateMap.put(String.valueOf(j), stonesInNextPit + 1);
                        gameTurnRepository.updateGameMove(2, gameIdValue);
                        stonesPickedUp.decrementAndGet();

                        log.info("Nice move! It is your turn again");
                    } else if (stonesPickedUp.get() == 0 && j!=14) {
                        gameTurnRepository.updateGameMove(1, gameIdValue);
                        log.info("Next players turn");
                    } else {
                        stateMap.put(String.valueOf(j), stonesInNextPit + 1);
                        stonesPickedUp.decrementAndGet();
                    }
                }
            }
        }

        return stateMap;
    }

}
