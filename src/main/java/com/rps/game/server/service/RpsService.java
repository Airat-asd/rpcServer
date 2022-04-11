package com.rps.game.server.service;

import com.rps.game.server.exception.RpcException;
import com.rps.game.server.model.Game;
import com.rps.game.server.model.Player;
import com.rps.game.server.repository.GameRepository;
import com.rps.game.server.repository.PlayerRepository;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RpsService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final GameService gameService;

    private final Set<ChannelHandlerContext> channelHandlerContextMap = Collections.newSetFromMap(new ConcurrentHashMap());

    public void registerOrAuthenticationPlayer(ChannelHandlerContext ctx, String request) {
        String name = request.replace(Command.REGISTER.getValue(), "").strip();
        if (!name.isEmpty()) {
            if (!this.channelHandlerContextMap.contains(ctx)) {
                this.channelHandlerContextMap.add(ctx);
                //Аутентификация не реализована
                Player player = saveNewOrMergeOldPlayer(ctx, name);
                try {
                    Game game = gameService.setPlayer(player);
                    if (game.playersAreReady()) {
                        sendMessage(game);
                    } else {
                        ctx.write("Search a player 2...\r\n");
                    }
                } catch (RpcException e) {
                    this.channelHandlerContextMap.remove(ctx);
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

    private void sendMessage(Game game) {
        ChannelHandlerContext ctx1 =
                getChannelHandlerContext(game.getPlayer1().getSession()).orElseThrow(() -> new RuntimeException("Session doesn't found"));
        ctx1.write(String.format("Player %s has been found. Start the battle.\r\n", game.getPlayer2().getName()));
        ctx1.flush();
        ChannelHandlerContext ctx2 =
                getChannelHandlerContext(game.getPlayer2().getSession()).orElseThrow(() -> new RuntimeException("Session doesn't found"));
        ctx2.write(String.format("Player %s has been found. Start the battle.\r\n", game.getPlayer1().getName()));
        ctx2.flush();
    }

    private Optional<ChannelHandlerContext> getChannelHandlerContext(String idChannel) {
        return this.channelHandlerContextMap.stream()
                .filter(ctx -> ctx.channel().id().asLongText().equals(idChannel))
                .findAny();
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

    public void battle(ChannelHandlerContext ctx, String request) throws InterruptedException {
        Optional<Game> gameOptional = gameRepository.getGameByChannelHandlerContext(ctx);
//        if (gameOptional.isPresent()) {
//            Game game = gameOptional.get();
//            Weapon weapon = Weapon.valueOf(Command.fromValue(request.trim()).name());
//            Player player = playerRepository.getByContext(ctx).get();
//            if (game.getPlayer1() == player) {
//                if (game.getWeapon1() == null) {
//                    game.setWeapon1(weapon);
//                } else {
//                    ctx.write("Your strike has already been made!\r\n");
//                }
//            } else if (game.getWeapon2() == null) {
//                game.setWeapon2(weapon);
//            } else {
//                ctx.write("Your strike has already been made!\r\n");
//            }

//            if (game.battleIsOver()) {
//                calculateResultOfGame(game);
//                gameRepository.update(game);
//                sendMessagesAfterGame(game);
//                Player player1 = game.getPlayer1();
//                Player player2 = game.getPlayer2();
//
//                if (player1.getGameResult().equals(Command.DRAW.getValue())) {
//                    log.info("Game is repeats");
//                    game.clearWeapon();
//                } else {
//                    sleep(5000);
////                    closeConnection(player1.getSession());
////                    closeConnection(player2.getSession());
//                    gameRepository.deletedGame(game);
//                }
//            }
//
//        } else {
//            ctx.write("You are not registered.\r\n");
//        }
    }

//    private void calculateResultOfGame(Game game) {
//        if (game.battleIsOver()) {
//            Weapon weapon1 = game.getWeapon1();
//            Weapon weapon2 = game.getWeapon2();
//            if (weapon1 == weapon2) {
//                game.getPlayer1().setGameResult(Command.DRAW.getValue());
//                game.getPlayer2().setGameResult(Command.DRAW.getValue());
//            } else {
//                boolean player1IsWin = (weapon1 != weapon2) && (weapon1.ordinal() == (weapon2.ordinal() + 1) % 3);
//                if (player1IsWin) {
//                    game.getPlayer1().setGameResult(Command.WIN.getValue());
//                    game.getPlayer2().setGameResult(Command.LOSE.getValue());
//
//                } else {
//                    game.getPlayer2().setGameResult(Command.WIN.getValue());
//                    game.getPlayer1().setGameResult(Command.LOSE.getValue());
//                }
//            }
//        }
//    }

//    private void sendMessagesAfterGame(Game game) {
//        Player player1 = game.getPlayer1();
//        Player player2 = game.getPlayer2();
//
//        if (player1.getGameResult().equals(Command.DRAW.getValue())) {
//            player1.getSession().write("Draw! The game repeats.\r\n");
//            player2.getSession().write("Draw! The game repeats.\r\n");
//        } else {
//            player1.getSession().write("You are " + player1.getGameResult() + "\r\n");
//            player2.getSession().write("You are " + player2.getGameResult() + "\r\n");
//        }
//        player1.getSession().flush();
//        player2.getSession().flush();
//    }

//    public void closeConnection(ChannelHandlerContext ctx) {
//        log.info("The connection is closed\r\n");
//        ChannelFuture future = ctx.write("Have a good day!\r\n");
//        future.addListener(ChannelFutureListener.CLOSE);
//    }

//        CompletableFuture<Optional<Player>> future =
//                CompletableFuture.supplyAsync(() -> playerRepository.searchFreePlayer(ctx));
//        Optional<Player> player = future.get();
//        return player2.get();
//    }
}