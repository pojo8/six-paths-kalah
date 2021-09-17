package com.inori.games.kalah.controller;

import com.inori.games.kalah.exceptions.IncorrectPlayerMoveException;
import com.inori.games.kalah.exceptions.InvalidPitException;
import com.inori.games.kalah.model.CreateGame;
import com.inori.games.kalah.model.GameState;
import com.inori.games.kalah.model.GameTurn;
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

import java.util.List;
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

        //converisons to ints
        Integer gameIdValue = Integer.valueOf(gameId);
        Integer pitIdValue = Integer.valueOf(pitId);

        // throw exception if kalah is selected pit 7 || pit 14
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
            } }catch (IncorrectPlayerMoveException ime){
            JSONObject body = new JSONObject();
            body.put("success", false);
            body.put("message", ime.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
        }

        // player 1 moves from pit 1-7 Player 2 8 - 14

        //collect all stones from the selected pit ID

        // increment each pit by one decrease available


        // If last stone remaining = 1 and next pit is kalah player gets to go again


        // LAst stone lands in empty
        GameState updatedGameState = new GameState();

        return new ResponseEntity<>(updatedGameState, HttpStatus.OK);
    }
}
