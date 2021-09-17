package com.inori.games.kalah.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GameState")
public class CreateGame {
    @Id
    @Column(name="id")
    @SequenceGenerator(name = "id_seq", sequenceName = "game_state_id_seq", initialValue = 777)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
    private Integer id;

    @Transient
    private String uri;
}
