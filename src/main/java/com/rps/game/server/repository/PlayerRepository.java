package com.rps.game.server.repository;

import com.rps.game.server.model.Player;
import java.util.Optional;

public interface PlayerRepository {

    Player insert(Player player);

//    Optional<Player> getByContext(ChannelHandlerContext ctx);

    Optional<Player> getByName(String name);
}
