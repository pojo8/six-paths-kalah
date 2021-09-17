package com.inori.games.kalah.repository;

import com.inori.games.kalah.model.GameState;
import com.inori.games.kalah.model.GameTurn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface GameTurnRepository extends JpaRepository<GameTurn, Long> {

    @Transactional
    List<GameTurn> findAll();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value= "insert into game_turn (game_id)  Values(:game_id) ", nativeQuery = true)
    void createGameTurnForGameId(Integer game_id);

    @Transactional
    @Query(value= "select next_player from game_turn where game_id = :game_id ", nativeQuery = true)
    Integer getNextPlayerByGameId(Integer game_id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value= "Update game_turn set next_player= :next_player where game_id =:game_id", nativeQuery = true)
    void updateGameMove(Integer next_player, Integer game_id);


}
