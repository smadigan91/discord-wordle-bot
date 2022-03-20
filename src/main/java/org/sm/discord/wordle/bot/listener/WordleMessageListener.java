package org.sm.discord.wordle.bot.listener;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class WordleMessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getTextChannel();
        System.out.println(channel.getName());
        if (channel.getName().contains("bot-test") && event.getMessage().getContentRaw().equals("hello wordlebot")) {
            String senderName = event.getAuthor().getName();
            channel.sendMessage("hello").queue(response -> {
                response.editMessage("hello " + senderName).queue();
            });
        }
    }
}
