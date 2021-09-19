package com.inori.games.kalah.controller;

import com.inori.games.kalah.component.MoveComponent;
import com.inori.games.kalah.exceptions.IncorrectPlayerMoveException;
import com.inori.games.kalah.exceptions.InvalidPitException;
import com.inori.games.kalah.model.CreateGame;
import com.inori.games.kalah.model.GameState;
import com.inori.games.kalah.repository.GameStateRepository;
import com.inori.games.kalah.repository.GameTurnRepository;
import com.inori.games.kalah.util.KalahUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.inori.games.kalah.common.AppConstants.*;


@Slf4j
@RestController
@RequestMapping("/games")
@CrossOrigin(origins="*")
public class KalahRestController {

    @Autowired
    private GameStateRepository gameStateRepository;

    @Autowired
    private GameTurnRepository gameTurnRepository;

    @Autowired
    private MoveComponent moveComponent;

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${server.host}")
    private String serverHost;

    @Value("${server.protocol}")
    private String serverProtocol;


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

        // Checking a valid move is made by the player
        try {
            inputMoveMadeByPlayer = KalahUtils.moveMadeBy(pitIdValue);
        } catch (InvalidPitException ipe) {
            JSONObject body = new JSONObject();
            body.put("success", false);
            body.put("message", ipe.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
        }

        // Fetching the specified player from Game Turn
        Integer playersTurn = gameTurnRepository.getNextPlayerByGameId(gameIdValue);
        log.info("The next move should be made by Player {}", playersTurn);

        // Checking that correct player makes the move
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

        // Getting the game state string and converting to hashmap
        String gameState = gameStateRepository.retrieveGameStateById(gameIdValue);

        HashMap<String, Object> stateMap = moveComponent.gameMove(gameState, pitId, gameId, playersTurn );

        // converting updated state for saving
        String stateJson = KalahUtils.convertHmapToJson(stateMap);

        gameStateRepository.saveUpdatedGameState(stateJson, gameIdValue);

        // checks win condition pits 1-6: 0 stones or pits 8-13:0
        String gameWinner = KalahUtils.hasGameCompleted(stateMap);
        if(gameWinner.length()>0){
            JSONObject body = new JSONObject();
            body.put("status", stateJson);
            body.put("id", gameId);
            body.put("url", url);
            body.put("message", "Good game the winner is: "+ gameWinner);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }

        GameState updatedGameState = new GameState();
        updatedGameState.setId(gameIdValue);
        updatedGameState.setUrl(url);
        updatedGameState.setStatus(stateMap);

        return new ResponseEntity<>(updatedGameState,HttpStatus.OK);
    }

    @GetMapping("/{gameId}/leader")
    public HttpEntity<? extends Object> findCurrentLeader(@PathVariable String gameId) {
        Integer gameIdValue = Integer.valueOf(gameId);

        // fetch game state
        String gameState = gameStateRepository.retrieveGameStateById(gameIdValue);

        // convert to hash map
        HashMap<String, Object> stateMap = KalahUtils.convertJsonToHmap(gameState);

        // compare kalah stone amount
        Integer player1KalahStones = (Integer) stateMap.get("7");
        Integer player2KalahStones = (Integer) stateMap.get("14");

        Integer playerLeading = player1KalahStones.compareTo(player2KalahStones);

        // Should Override/ custom comparable
        if(playerLeading.equals(1)){
            JSONObject body = new JSONObject();
            body.put("message", "The player currently in the lead is player 1");
            return new ResponseEntity<>( body, HttpStatus.OK);

        } else if (playerLeading.equals(-1)){
            JSONObject body = new JSONObject();
            body.put("message", "The player currently in the lead is player 2");
            return new ResponseEntity<>( body, HttpStatus.OK);

        } else{
            JSONObject body = new JSONObject();
            body.put("message", "Both players are tied with the same number of stones in their Kalah");
            return new ResponseEntity<>( body, HttpStatus.OK);
        }

    }

}
