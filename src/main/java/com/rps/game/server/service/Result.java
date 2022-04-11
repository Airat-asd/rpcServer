package com.rps.game.server.service;

import com.rps.game.server.model.Player;
import lombok.Builder;

@Builder
public class Result {

    private Player player;
    private boolean isWin;
}
