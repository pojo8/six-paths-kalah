package com.inori.games.kalah.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="GameState")
@Table(name = "GameState")
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
public class GameState {

    @Id
    @Column(name="id")
    @SequenceGenerator(name = "id_seq", sequenceName = "game_state_id_seq", initialValue = 777)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
    private Integer id;

    private String url;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Map<String, String> status = new HashMap<>();




//    // Player 1 pits
//    private Integer one;
//
//    private Integer two;
//
//    private Integer three;
//
//    private Integer four;
//
//    private Integer five;
//
//    private Integer six;
//
//    // Player 1 Kalah
//    private Integer seven;
//
//
//    // Player 2 pits
//    private Integer eight;
//
//    private Integer nine;
//
//    private Integer ten;
//
//    private Integer eleven;
//
//    private Integer twelve;
//
//    private Integer thirteen;
//
//    // Player 2 Kallah
//    private Integer fourteen;

}
