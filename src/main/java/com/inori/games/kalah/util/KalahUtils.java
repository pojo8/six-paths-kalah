package com.inori.games.kalah.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inori.games.kalah.exceptions.InvalidPitException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.inori.games.kalah.common.AppConstants.PLAYER1;
import static com.inori.games.kalah.common.AppConstants.PLAYER2;
import static com.inori.games.kalah.common.AppConstants.PLAYER_1_NO_KALAH;
import static com.inori.games.kalah.common.AppConstants.PLAYER_2_NO_KALAH;

@Slf4j
public class KalahUtils {
    /**
     * A method that randomly selects the first move
     * @return string Player1 or Player2
     */
    public static String selectFirstMove(){
        List<String> players = new ArrayList<>();
        players.add(PLAYER1);
        players.add(PLAYER2);

        Random rand = new Random();
        log.info("Selecting a player to take the first move ...");
        return players.get(rand.nextInt(players.size()));

    }

    /**
     * A method that takes in
     * @param pitId
     * and determines who the move was made by
     * @return integer playerNumber
     */
    public static Integer moveMadeBy(Integer pitId){
        if(pitId.equals(1) || pitId.equals(2) || pitId.equals(3) || pitId.equals(4) || pitId.equals(5)|| pitId.equals(6)){
            return 1;
        } else if(pitId.equals(8) || pitId.equals(9) || pitId.equals(10) || pitId.equals(11) || pitId.equals(12)|| pitId.equals(13)){
            return 2;
        } else if(pitId.equals(7) || pitId.equals(14) ){
            throw new InvalidPitException("The pit selected cannot be a Kalah with pit id 7 or 14");
        } else {
            throw new InvalidPitException("The pit selected must be either 1 - 6 for player 1 or 8 - 13 for player 2");
        }
    }

    /**
     * A method that takes
     * @param gameState
     *  a json string and
     * @return the equivalent hashMap
     */
    public static HashMap<String, Object> convertJsonToHmap(String gameState) {
        ObjectMapper mapper = new ObjectMapper();

        HashMap<String, Object> stateMap = null;
        try {
            stateMap = mapper.readValue(gameState, HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return stateMap;
    }


    public static String convertHmapToJson(HashMap<String, Object> stateMap) {
        ObjectMapper mapper = new ObjectMapper();

        String stateJson = null;

        try {
             stateJson = mapper.writeValueAsString(stateMap);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return stateJson;
    }

    /**
     * Method that determines the winner of the game
     * taking in
     * @param stateMap
     * and
     * @return winning player
     */
    public static String hasGameCompleted(HashMap<String, Object> stateMap) {
        Integer player1Total =0;
        Integer player2Total =0;

        for (String pitId:
             PLAYER_1_NO_KALAH) {
            Integer stonesFound = (Integer) stateMap.get(pitId);
            player1Total += stonesFound;
        }

        for (String pitId:
                PLAYER_2_NO_KALAH) {
            Integer stonesFound = (Integer) stateMap.get(pitId);
            player2Total += stonesFound;
        }

        if(player1Total.equals(0) || player2Total.equals(0)){
            log.info("A player has run out of stones");

            Integer player1KalahStones = (Integer) stateMap.get("7");
            Integer player2KalahStones = (Integer) stateMap.get("14");

            Integer winner = player1KalahStones.compareTo(player2KalahStones);
            if(winner.equals(1)){
                return PLAYER1;
            } else {
                return PLAYER2;
            }
        }

        return "";
    }
}
