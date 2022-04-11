package com.rps.game.server.repository;

import com.rps.game.server.model.Game;
import com.rps.game.server.model.Player;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public class GameRepositoryImpl implements GameRepository {

    private final List<Game> gameList = new LinkedList<>();

    @Override
    public void insert(Game game) {
        gameList.add(game);
    }

    @Override
    public boolean update(Game game) {
        int index = gameList.indexOf(game);
        if (index != -1) {
            gameList.set(index, game);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public Optional<Game> getGameByPlayer(Player player) {
        return gameList.stream()
                .filter(game -> game.getPlayer1() == player || game.getPlayer2() == player)
                .findAny();
    }

    @Override
    public Optional<Game> getGameByChannelHandlerContext(ChannelHandlerContext ctx) {
        return null;
//        return gameList.stream()
//                .filter(game -> game.getPlayer1().getSession() == ctx || game.getPlayer2().getSession() == ctx)
//                .findAny();
    }

    @Override
    public boolean deletedGame(Game game) {
        return gameList.remove(game);
    }


}
