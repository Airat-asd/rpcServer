package com.rps.game.server.service;

import lombok.Getter;

@Getter
public enum Command {
    EXIST("exist"),
    REGISTER("register"),
    NOT_FOUND("not found"),
    ROCK("rock"),
    PAPER("paper"),
    SCISSORS("scissors"),
    DRAW("draw"),
    WIN("win"),
    LOSE("lose");

    private final String value;

    Command(final String value) {
        this.value = value;
    }

    public static Command fromValue(String value) {
        for (final Command command : values()) {
            if (command.value.equalsIgnoreCase(value)) {
                return command;
            }
        }
        return NOT_FOUND;
    }
}