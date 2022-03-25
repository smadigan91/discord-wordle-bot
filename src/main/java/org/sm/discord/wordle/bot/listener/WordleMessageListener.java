package org.sm.discord.wordle.bot.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.service.WordleBotService;
import org.sm.discord.wordle.bot.util.WordleUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


public class WordleMessageListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WordleMessageListener.class);

    @Autowired
    WordleBotService botService;


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getTextChannel();
        System.out.println(channel.getName());
        if (channel.getName().contains("bot-test")) {
            String problemId;
            Message message = event.getMessage();
            String messageContent = message.getContentRaw();
            // if it's a valid wordle attempt, save it
            if ((problemId = WordleUtil.getProblemId(messageContent)) != null) {
                logger.info("Wordle attempt detected, processing");
                botService.indexNewDiscordMessage(message, problemId);
                try {
                    botService.updateProblemSolutions();
                } catch (IOException e) {
                    logger.error("Error updating problem solutions", e);
                }
            }
            //handle slash commands and stuff
        }
    }
}
