package com.rps.game.server.service;

import com.rps.game.server.exception.RpcException;
import com.rps.game.server.model.Game;
import com.rps.game.server.model.Player;
import com.rps.game.server.repository.GameRepository;
import com.rps.game.server.repository.PlayerRepository;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RpsService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    private final Map<String, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<>();
    private Game game = new Game(UUID.randomUUID().toString());

    public void registerOrAuthenticationPlayer(ChannelHandlerContext ctx, String request) {
        String name = request.replace(Command.REGISTER.getValue(), "").strip();
        if (!name.isEmpty()) {
            if (!this.channelHandlerContextMap.containsValue(ctx)) {
                this.channelHandlerContextMap.put(ctx.channel().id().asLongText(), ctx);
                //Аутентификация не реализована
                Player player = saveNewOrMergeOldPlayer(ctx, name);
                try {
                    Game game = setPlayer(player);
                    if (game.playersAreReady()) {
                        sendMessage(game);
                    } else {
                        ctx.write("Search a player 2...\r\n");
                    }
                } catch (RpcException e) {
                    this.channelHandlerContextMap.remove(ctx.channel().id().asLongText());
                    log.warn(e.getMessage());
                    ctx.write(e.getMessage() + "\r\n");
                }
            } else {
                ctx.write("You are already registered!\r\n");
            }
        } else {
            ctx.write("Name is empty, repeat please.\r\n");
        }
    }

    public synchronized Game setPlayer(Player player) throws RpcException {
        if (this.game.playersAreReady()) {
            this.game = new Game(UUID.randomUUID().toString());
        }
        this.game.setPlayers(player);

        if (this.game.playersAreReady()) {
            gameRepository.insert(this.game);
        }

        return game;
    }

    private void sendMessage(Game game) {
        ChannelHandlerContext ctx1 = channelHandlerContextMap.get(game.getPlayer1().getSession());
        ctx1.write(String.format("Player %s has been found. Start the battle (enter command: paper, rock, scissors). \r\n",
                game.getPlayer2().getName()));
        ctx1.flush();
        ChannelHandlerContext ctx2 = channelHandlerContextMap.get(game.getPlayer2().getSession());
        ctx2.write(String.format("Player %s has been found. Start the battle (enter command: paper, rock, scissors). \r\n",
                game.getPlayer1().getName()));
        ctx2.flush();
    }

    public Player saveNewOrMergeOldPlayer(ChannelHandlerContext ctx, String name) {
        Player player = Player
                .builder()
                .id(UUID.randomUUID().toString())
                .session(ctx.channel().id().asLongText())
                .name(name)
                .build();
        playerRepository.insert(player);

        return player;
    }

    public void battle(ChannelHandlerContext ctx, String request) {
        Optional<Player> playerOptional = playerRepository.getPlayerBySession(ctx);
        Player player =
                playerOptional
                        .orElseThrow(() ->
                                new RpcException("The player with " + ctx.channel().id().asLongText() + " session was not found"));
        Optional<Game> gameOptional = gameRepository.getGameByPlayer(player);
        Game game = gameOptional.orElseThrow(() -> new RpcException("The player is not found in the game"));
        Weapon weapon = Weapon.valueOf(Command.fromValue(request.trim()).name());
        setWeaponToGame(ctx, player, game, weapon);
        game = gameRepository.update(game);
        if (game.battleIsOver()) {
            finishBattle(game);
        }
    }

    private void finishBattle(Game game) {
        calculateResultOfGame(game);
        sendMessagesAfterGame(game);
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        if (player1.getGameResult().equals(Command.DRAW.getValue())) {
            log.info("Game is repeats");
            game.clearWeapon();
            gameRepository.update(game);
        } else {
            closeConnection(channelHandlerContextMap.get(player1.getSession()));
            channelHandlerContextMap.remove(player1.getSession());
            closeConnection(channelHandlerContextMap.get(player2.getSession()));
            channelHandlerContextMap.remove(player2.getSession());
            gameRepository.deleteGame(game);
        }
    }

    private void setWeaponToGame(ChannelHandlerContext ctx, Player player, Game game, Weapon weapon) {
        if (game.getPlayer1().getId().equals(player.getId())) {
            if (game.getWeapon1() == null) {
                game.setWeapon1(weapon);
            } else {
                ctx.write("Your strike has already been made!\r\n");
            }
        } else if (game.getWeapon2() == null) {
            game.setWeapon2(weapon);
        } else {
            ctx.write("Your strike has already been made!\r\n");
        }
    }

    private void calculateResultOfGame(Game game) {
        if (game.battleIsOver()) {
            Weapon weapon1 = game.getWeapon1();
            Weapon weapon2 = game.getWeapon2();
            if (weapon1 == weapon2) {
                game.getPlayer1().setGameResult(Command.DRAW.getValue());
                game.getPlayer2().setGameResult(Command.DRAW.getValue());
            } else {
                if (weapon1.ordinal() == (weapon2.ordinal() + 1) % 3) {
                    game.getPlayer1().setGameResult(Command.WIN.getValue());
                    game.getPlayer2().setGameResult(Command.LOSE.getValue());

                } else {
                    game.getPlayer2().setGameResult(Command.WIN.getValue());
                    game.getPlayer1().setGameResult(Command.LOSE.getValue());
                }
            }
        }
    }

    private void sendMessagesAfterGame(Game game) {
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();
        ChannelHandlerContext ctxPlayer1 = channelHandlerContextMap.get(player1.getSession());
        ChannelHandlerContext ctxPlayer2 = channelHandlerContextMap.get(player2.getSession());
        if (player1.getGameResult().equals(Command.DRAW.getValue())) {
            ctxPlayer1.write("Draw! The game repeats.\r\n");
            ctxPlayer2.write("Draw! The game repeats.\r\n");
        } else {
            ctxPlayer1.write("You are " + player1.getGameResult() + "\r\n");
            ctxPlayer2.write("You are " + player2.getGameResult() + "\r\n");
        }
        ctxPlayer1.flush();
        ctxPlayer2.flush();
    }

    public void closeConnection(ChannelHandlerContext ctx) {
        log.info("The connection is closed\r\n");
        ChannelFuture future = ctx.write("Have a good day!\r\n");
        ctx.flush();
        future.addListener(ChannelFutureListener.CLOSE);
    }
}