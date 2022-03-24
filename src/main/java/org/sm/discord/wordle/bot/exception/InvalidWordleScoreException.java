package org.sm.discord.wordle.bot.exception;

public class InvalidWordleScoreException extends DiscordWordleBotRuntimeException {

    public InvalidWordleScoreException() {
        super();
    }

    public InvalidWordleScoreException(String reason) {
        super(reason);
    }

    public InvalidWordleScoreException(Throwable cause) {
        super(cause);
    }

    public InvalidWordleScoreException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
