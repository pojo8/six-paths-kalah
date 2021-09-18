package com.inori.games.kalah.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inori.games.kalah.exceptions.IncorrectPlayerMoveException;
import com.inori.games.kalah.exceptions.InvalidPitException;
import com.inori.games.kalah.model.CreateGame;
import com.inori.games.kalah.model.GameState;
import com.inori.games.kalah.model.GameTurn;
import com.inori.games.kalah.repository.GameStateRepository;
import com.inori.games.kalah.repository.GameTurnRepository;
import com.inori.games.kalah.util.KalahUtils;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.inori.games.kalah.common.AppConstants.*;


@Slf4j
@RestController
@RequestMapping("/games")
@CrossOrigin(origins="*")
public class KalahRestController {

    @Autowired
    GameStateRepository gameStateRepository;

    @Autowired
    GameTurnRepository gameTurnRepository;

    @Value("${server.port}")
    Integer serverPort;

    @Value("${server.host}")
    String serverHost;

    @Value("${server.protocol}")
    String serverProtocol;



    @GetMapping("/list")
    public List<GameState> listOpenGames() {
        return gameStateRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<CreateGame> newGame(){
        // Creating game and turn table entries
        Integer gameId = gameStateRepository.createGameAndReturnGameId();
        gameTurnRepository.createGameTurnForGameId(gameId);

        log.info("Created a new game with the following Game Id: {}", gameId);

        String url = serverProtocol + COLON + SLASH + SLASH + serverHost + COLON + serverPort.toString()+ SLASH + GAMES + SLASH + gameId.toString();
        log.debug("The corresponding game url is: {}", url);

        // setting the uri for later use
        try {
            gameStateRepository.saveGameUrl(url, Integer.valueOf(gameId));
        } catch (Exception e){
            log.error("An error occurred whilst saving the game URL: {}", e.getMessage());
            // throw new exception
        }

        CreateGame generatedGame = new CreateGame();
        generatedGame.setId(gameId);
        generatedGame.setUri(url);

        // Selecting player to go first - maybe emailing moves for future
        String firstPlayer = KalahUtils.selectFirstMove();
        log.info("The first move goes to: {}", firstPlayer);

        // Sets the first player for the game
        if(firstPlayer.equals(PLAYER1)){
            gameTurnRepository.updateGameMove(1, gameId);
        } else {
            gameTurnRepository.updateGameMove(2, gameId);
        }

        return new ResponseEntity(generatedGame, HttpStatus.CREATED);

    }

    @PutMapping("/{gameId}/pits/{pitId}")
    public HttpEntity<? extends Object> moveStones(@PathVariable String gameId, @PathVariable String pitId) {

        Integer gameIdValue = Integer.valueOf(gameId);
        Integer pitIdValue = Integer.valueOf(pitId);

        String url = gameStateRepository.retrieveUrlById(gameIdValue);
        Integer inputMoveMadeByPlayer;
        try {
            inputMoveMadeByPlayer = KalahUtils.moveMadeBy(pitIdValue);
        } catch (InvalidPitException ipe) {
            JSONObject body = new JSONObject();
            body.put("success", false);
            body.put("message", ipe.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
        }

        // Fetching the specified played
        Integer playersTurn = gameTurnRepository.getNextPlayerByGameId(gameIdValue);
        log.info("The next move should be made by Player {}", playersTurn);

        // Testing the correct player makes the move
        try{
            if (!playersTurn.equals(inputMoveMadeByPlayer)) {
                throw new IncorrectPlayerMoveException("It is currently not your turn. Please allow the other player to make their move");
            }
        }catch (IncorrectPlayerMoveException ime){
            JSONObject body = new JSONObject();
            body.put("success", false);
            body.put("message", ime.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
        }

        // Getting the game state string and converitn gto hasmap
        String gameState = gameStateRepository.retrieveGameStateById(gameIdValue);

        AtomicInteger stonesPickedUp = new AtomicInteger(0);


        HashMap<String, Object> stateMap = KalahUtils.convertJsonToHmap(gameState);

        Integer stones = (Integer) stateMap.get(pitId);
        stonesPickedUp.lazySet(stones);

        // Clear stones in that pit
        stateMap.put(pitId, 0);
        for(int i=pitIdValue+1; i<=stones+2; i++){
            log.info("Adding a stone to pit {}", i);
            // gets the number in the pit

            if(i == (stones+2) && !(stonesPickedUp.getAcquire() ==0)){
                i=0;
            } else {
                Integer stonesInNextPit = (Integer) stateMap.get(String.valueOf(i));

                // Test granted that next move is into an empty pit
                if (stonesPickedUp.get() == 1 && stonesInNextPit == 0) {
                    log.info("Great move! Capturing opponents stones");
                    Integer capturedStones = 0;
                    if (playersTurn.equals(1)) {
                        capturedStones = (Integer) stateMap.get(PLAYER_2_PITS.get((PLAYER_2_PITS.size() - 1) - i));
                        stateMap.put(String.valueOf(i), capturedStones + 1);

                        gameTurnRepository.updateGameMove(2, gameIdValue);
                        stonesPickedUp.decrementAndGet();
                    } else {
                        capturedStones = (Integer) stateMap.get(PLAYER_1_PITS.get((PLAYER_1_PITS.size() - 1) - i));
                        stateMap.put(String.valueOf(i), capturedStones + 1);

                        gameTurnRepository.updateGameMove(1, gameIdValue);
                        stonesPickedUp.decrementAndGet();
                    }
                    log.info("Adding {} captured stones plus your last stone to pit: {}", capturedStones, i);
                } else if (stonesPickedUp.get() == 1 && i == 7 || i == 14) {
                    if (playersTurn.equals(1)) {
                        // case of player 1
                        stateMap.put(String.valueOf(i), stonesInNextPit + 1);
                        gameTurnRepository.updateGameMove(1, gameIdValue);
                        stonesPickedUp.decrementAndGet();

                    } else {
                        // case for player 2
                        stateMap.put(String.valueOf(i), stonesInNextPit + 1);
                        gameTurnRepository.updateGameMove(2, gameIdValue);
                        stonesPickedUp.decrementAndGet();

                    }
                    log.info("Nice move! It is your turn again");
                } else if (stonesPickedUp.get() == 0) {
                    if (playersTurn.equals(1)) {
                        // case of player 1
                        gameTurnRepository.updateGameMove(2, gameIdValue);

                    } else {
                        // case for player 2
                        gameTurnRepository.updateGameMove(1, gameIdValue);
                    }
                    log.info("Next players turn");
                } else {
                    stateMap.put(String.valueOf(i), stonesInNextPit + 1);
                    stonesPickedUp.decrementAndGet();
                }
            }

        }

        // converting updated state for saving
        String stateJson = KalahUtils.convertHmapToJson(stateMap);
        gameStateRepository.saveUpdatedGameState(stateJson, gameIdValue);

        GameState updatedGameState = new GameState();
        updatedGameState.setId(gameIdValue);
        updatedGameState.setUrl(url);
        updatedGameState.setStatus(stateMap);

        return new ResponseEntity<>(updatedGameState, HttpStatus.OK);
    }
}
