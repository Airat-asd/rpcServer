package com.rps.game.server.model;

import com.rps.game.server.service.Weapon;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    private String name;
    @Id
    private String id;
    private String gameResult;
    private Weapon weapon;
    private String session;
    @OneToOne
    private Game game;
}