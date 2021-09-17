package com.inori.games.kalah.repository;

import com.inori.games.kalah.model.CreateGame;
import com.inori.games.kalah.model.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface GameStateRepository extends JpaRepository<GameState, Long> {

    @Transactional
    List<GameState> findAll();

    @Transactional
    @Query(value= "insert into game_state (status)  Values(  json_build_object('1',6,'2',6,'3',6,'4',6,'5',6,'6',6,'7',0,'8',6,'9',6,'10',6,'11',6,'12',6,'13',6,'14',0) ) returning game_state.id", nativeQuery = true)
    Integer createGameAndReturnGameId();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value= "Select nextval('game_id_seq') as id", nativeQuery = true)
    Integer getNextGameId();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value= "Update game_state set url= :url where id =:id", nativeQuery = true)
    Integer saveGameUrl(String url, Integer id);

    public static interface GeneratedID {
        String getId();
    }

}
