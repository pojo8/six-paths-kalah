package com.inori.games.kalah.model;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
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
        @TypeDef(name = "json", typeClass = JsonStringType.class)
})
public class GameState {

    @Id
    @Column(name="id")
    @SequenceGenerator(name = "id_seq", sequenceName = "game_state_id_seq", initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
    private Integer id;

    @Column(name="url")
    private String url;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    @Convert(disableConversion = true)
    private Map<String, Object> status = new HashMap<>();
//    private Map<String, String> status = new HashMap<>();

}
