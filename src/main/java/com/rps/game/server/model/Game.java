package com.rps.game.server.model;

import com.rps.game.server.exception.RpcException;
import com.rps.game.server.service.Weapon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    private String id;
    @OneToOne
    private Player player1;
    @OneToOne
    private Player player2;
    private String weapon1;
    private String weapon2;

    public boolean setPlayers(Player player) {
        if (player1 == null) {
            this.player1 = player;
            return true;
        } else {
            if (player2 == null) {
                if (!this.player1.getName().equals(player.getName())) {
                    this.player2 = player;
                    return true;
                } else {
                   throw new RpcException("An attempt to add two identical players to the game");
                }
            }
        }

        return false;
    }

    public boolean playersAreReady() {
        return player2 != null;
    }

    public boolean battleIsOver() {
        return weapon1 != null && weapon2 != null;
    }

    public void clearWeapon() {
        weapon1 = null;
        weapon2 = null;
    }
}