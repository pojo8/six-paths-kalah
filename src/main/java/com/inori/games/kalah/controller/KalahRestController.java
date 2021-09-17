package com.inori.games.kalah.controller;

import com.inori.games.kalah.model.CreateGame;
import com.inori.games.kalah.model.GameState;
import com.inori.games.kalah.repository.GameStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.inori.games.kalah.common.AppConstants.SLASH;
import static com.inori.games.kalah.common.AppConstants.COLON;
import static com.inori.games.kalah.common.AppConstants.GAMES;


@Slf4j
@RestController
@RequestMapping("/games")
@CrossOrigin(origins="*")
public class KalahRestController {

    @Autowired
    GameStateRepository gameStateRepository;

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
        Integer gameId = gameStateRepository.createGameAndReturnGameId();

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
        generatedGame.setUrl(url);

        return new ResponseEntity(generatedGame, HttpStatus.CREATED);

    }

}
