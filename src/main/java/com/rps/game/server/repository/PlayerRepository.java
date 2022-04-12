package com.rps.game.server.repository;

import com.rps.game.server.model.Player;
import io.netty.channel.ChannelHandlerContext;

import java.util.Optional;

public interface PlayerRepository {

    Player insert(Player player);

    Optional<Player> getPlayerBySession(ChannelHandlerContext ctx);
}