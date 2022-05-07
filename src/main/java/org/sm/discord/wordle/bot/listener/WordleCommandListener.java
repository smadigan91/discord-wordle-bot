package org.sm.discord.wordle.bot.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.service.WordleStatsService;

public class WordleCommandListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WordleMessageListener.class);

    private final WordleStatsService statsService;

    public WordleCommandListener(WordleStatsService statsService) {
        super();
        this.statsService = statsService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getTextChannel();
        System.out.println(channel.getName());
        if (channel.getName().contains("bot-test")) {
            Message message = event.getMessage();
            String messageContent = message.getContentRaw();
            if (messageContent.equals("test")) {
                String embedString = statsService.getUserLeaderboard();
                channel.sendMessage("Top Users By Total Score").complete();
                channel.sendMessage(embedString).complete();
//                MessageEmbed embed = new MessageEmbed(null, "Top Users By Total Score", embedString, EmbedType.RICH, null, 0, null, null, null, null, null, null, null);
//                channel.sendMessageEmbeds(embed).complete();
            }
        }
    }
}
