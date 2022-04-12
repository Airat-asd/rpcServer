package com.rps.game.server.repository;

import com.rps.game.server.model.Game;
import com.rps.game.server.model.Player;

import java.util.Optional;

public interface GameRepository {

    Game getGameById(String id);

    void insert(Game game);

    Game update(Game game);

    Optional<Game> getGameByPlayer(Player player);

    void deleteGame(Game game);
}