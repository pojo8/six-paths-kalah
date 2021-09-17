package com.inori.games.kalah.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GameTurn")
@Entity(name = "GameTurn")
public class GameTurn {

    @Id
    @Column(name="turn_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name= "next_player")
    private Integer nextPlayer;
}
