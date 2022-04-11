package com.rps.game.server.service;

import com.rps.game.server.exception.RpcException;
import com.rps.game.server.model.Game;
import com.rps.game.server.model.Player;
import com.rps.game.server.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final List<Player> playerList = new LinkedList<>();
    private byte count = 0;
    private Game game = new Game();

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game setPlayer(Player player) throws RpcException {
        if (this.game.playersAreReady()) {
            this.game = new Game();
        }
        this.game.setPlayers(player);

        if (this.game.playersAreReady()) {
            gameRepository.insert(this.game);
        }

        return game;
    }

    public Result battle(Player player, Weapon weapon) {
        if (count == 2) {
            throw new RpcException("The weapons ran out");
        } else if (playerList.contains(player)) {
            count++;
            int indexPlayer = playerList.indexOf(player);
            player = playerList.get(indexPlayer);
            player.setWeapon(weapon);
            playerList.set(indexPlayer, player);
        } else {
            throw new RpcException("The player is not found in this game");
        }

        if (count == 2) {
            Weapon weapon1 = playerList.get(0).getWeapon();
            Weapon weapon2 = playerList.get(1).getWeapon();
            return Result.builder()
                    .player(player)
                    .isWin((weapon1 != weapon2) && (weapon1.ordinal() == (weapon2.ordinal() + 1) % 3))
                    .build();
        }

        return Result.builder().build();
    }
}