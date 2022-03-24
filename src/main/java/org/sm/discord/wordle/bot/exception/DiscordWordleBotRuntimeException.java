package org.sm.discord.wordle.bot.exception;

public class DiscordWordleBotRuntimeException extends RuntimeException {

    public DiscordWordleBotRuntimeException() {
        super();
    }

    public DiscordWordleBotRuntimeException(String reason) {
        super(reason);
    }

    public DiscordWordleBotRuntimeException(Throwable cause) {
        super(cause);
    }

    public DiscordWordleBotRuntimeException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
