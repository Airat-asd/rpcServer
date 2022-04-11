package com.rps.game.server.controller;

import com.rps.game.server.RpsGameServerApplication;
import com.rps.game.server.service.Command;
import com.rps.game.server.service.RpsService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
@Sharable
public class TelnetController extends SimpleChannelInboundHandler<String> {

    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpsGameServerApplication.class);
    private final RpsService rpsService = context.getBean(RpsService.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.write("Welcome to the game - Rock, Paper, Scissors!\r\n");
        ctx.write("To start the game, please register, to do this, enter command \"register [YOU NAME]\" and press ENTER:\r\n");
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws InterruptedException {
        log.info("channelRead0");
        requestHandler(ctx, request);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void requestHandler(ChannelHandlerContext ctx, String request) throws InterruptedException {
        if (request.contains(Command.REGISTER.getValue())) {
            rpsService.registerOrAuthenticationPlayer(ctx, request);
        } else if (request.contains(Command.SCISSORS.getValue()) || request.contains(Command.ROCK.getValue()) ||
                request.contains(Command.PAPER.getValue())) {
            rpsService.battle(ctx, request);
        } else if (request.contains(Command.EXIST.getValue())) {
//            rpsService.closeConnection(ctx);
        }
    }
}