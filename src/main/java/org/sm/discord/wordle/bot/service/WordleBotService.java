package org.sm.discord.wordle.bot.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.util.DiscordConstants;
import org.sm.discord.wordle.bot.util.WordleUtil;
import org.sm.discord.wordle.persistence.repository.AttemptRepository;
import org.sm.discord.wordle.persistence.repository.DiscordUserRepository;
import org.sm.discord.wordle.persistence.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service("wordleBotService")
public class WordleBotService {

    private static final Logger logger = LoggerFactory.getLogger(WordleBotService.class);

    private final JDA discordBot;

    private DiscordUserRepository userRepo;

    private ProblemRepository problemRepo;

    private AttemptRepository attemptRepo;

    @Autowired
    public WordleBotService(JDA discordBot, DiscordUserRepository userRepo,
                            ProblemRepository problemRepo, AttemptRepository attemptRepo) {
        this.discordBot = discordBot;
        this.userRepo = userRepo;
        this.problemRepo = problemRepo;
        this.attemptRepo = attemptRepo;
    }

    public void init() throws IOException {
        Map<String, String> wordleIndex = WordleUtil.getAnswerIndex();
        int startingProblemId = 200;
        for (String problemId : wordleIndex.keySet()) {
            // only need to worry about problems we haven't indexed yet
            if (!problemRepo.existsById(problemId)) {
                startingProblemId = Integer.parseInt(problemId);
                break;
            }
        }
        if (startingProblemId < WordleUtil.FIRST_WORDLE_CHANNEL_PROBLEM_ID) {
            // we need to look at messages in spam too
            // then wordle stuff
            indexSpamHistory();
            indexWordleHistory();
        } else {
            //just the wordle stuff
            indexWordleHistory();
        }
    }

    public void indexSpamHistory() {
        TextChannel spam = discordBot.getTextChannelById(DiscordConstants.SPAM_CHANNEL_ID);
        if (spam != null) {
            spam.getHistoryAfter(DiscordConstants.FIRST_SPAM_WORDLE_BLOCK_MESSAGE_ID, 100).queue(result -> {
                result.getRetrievedHistory().forEach(message -> {
                    //need some transactional method that acts on the message I can call here
                });
            });
            spam.getHistoryAfter(DiscordConstants.SECOND_SPAM_WORDLE_BLOCK_MESSAGE_ID, 15).queue(result -> {
                result.getRetrievedHistory().forEach(message -> {
                    //need some transactional method that acts on the message I can call here
                });
            });
        }
    }

    public void indexWordleHistory() {
        TextChannel wordle = discordBot.getTextChannelById(DiscordConstants.WORDLE_CHANNEL_ID);
        Set<String> problems = new HashSet<>();
        if (wordle != null) {
            wordle.getIterableHistory().forEach(message -> {
                String messageContent = message.getContentRaw();
                String problemNumber = WordleUtil.getWordleNumber(messageContent);
                if (problemNumber != null) {
                    problems.add(problemNumber);
                }
            });
            problems.forEach(s -> {
                logger.info("Processed Wordle " + s);
            });
        }
    }

    public void getWordleHistory() {
        TextChannel wordle = discordBot.getTextChannelById(DiscordConstants.WORDLE_CHANNEL_ID);
        Set<String> problems = new HashSet<>();
        if (wordle != null) {
            wordle.getIterableHistory().forEach(message -> {
                String messageContent = message.getContentRaw();
                String problemNumber = WordleUtil.getWordleNumber(messageContent);
                if (problemNumber != null) {
                    problems.add(problemNumber);
                }
            });
            problems.forEach(s -> {
                logger.info("Processed Wordle " + s);
            });
        }
    }

    public void getSpamHistory() {
        TextChannel spam = discordBot.getTextChannelById(DiscordConstants.SPAM_CHANNEL_ID);
        if (spam != null) {
            spam.getHistoryAfter(DiscordConstants.FIRST_SPAM_WORDLE_BLOCK_MESSAGE_ID, 100).queue(result -> {
                Message firstWordleMessage = result.getMessageById(DiscordConstants.FIRST_SPAM_WORDLE_MESSAGE_ID);
            });
            spam.getHistoryAfter(DiscordConstants.SECOND_SPAM_WORDLE_BLOCK_MESSAGE_ID, 15).queue(result -> {
                Message lastWordleMessage = result.getMessageById(DiscordConstants.LAST_SPAM_WORDLE_MESSAGE_ID);
            });
        }
    }
}
