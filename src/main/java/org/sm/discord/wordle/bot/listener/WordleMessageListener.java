package org.sm.discord.wordle.bot.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.service.WordleMessageService;

import java.io.IOException;

public class WordleMessageListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WordleMessageListener.class);

    private final WordleMessageService botService;

    public WordleMessageListener(WordleMessageService msgService) {
        super();
        this.botService = msgService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getTextChannel();
        System.out.println(channel.getName());
        if (channel.getName().contains("bot-test")) {
            Message message = event.getMessage();
            Double wordleScore = botService.indexDiscordMessage(message);
            if (wordleScore != null) {
                try {
                    botService.updatePastProblemSolutions();
                } catch (IOException e) {
                    logger.error("Error updating problem solutions", e);
                }
                channel.sendMessage("Your score is " + wordleScore).queue();
            }
        }
    }
}
