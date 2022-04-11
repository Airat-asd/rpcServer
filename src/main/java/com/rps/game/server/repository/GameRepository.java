package com.rps.game.server.repository;

import com.rps.game.server.model.Game;
import com.rps.game.server.model.Player;
import io.netty.channel.ChannelHandlerContext;

import java.util.Optional;

public interface GameRepository {

    void insert(Game game);

    boolean update(Game game);

    Optional<Game> getGameByPlayer(Player player);

    Optional<Game> getGameByChannelHandlerContext(ChannelHandlerContext ctx);

    boolean deletedGame(Game game);
}
