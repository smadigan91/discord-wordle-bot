package org.sm.discord.wordle.bot.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.util.DiscordConstants;
import org.sm.discord.wordle.bot.util.WordleUtil;
import org.sm.discord.wordle.persistence.entity.Attempt;
import org.sm.discord.wordle.persistence.entity.DiscordUser;
import org.sm.discord.wordle.persistence.entity.Problem;
import org.sm.discord.wordle.persistence.repository.AttemptRepository;
import org.sm.discord.wordle.persistence.repository.DiscordUserRepository;
import org.sm.discord.wordle.persistence.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("wordleBotService")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WordleBotService {

    private static final Logger logger = LoggerFactory.getLogger(WordleBotService.class);

    private final JDA discordBot;

    private final DiscordUserRepository userRepo;

    private final ProblemRepository problemRepo;

    private final AttemptRepository attemptRepo;

    private Map<String, String> problemSolutions;

    @Autowired
    public WordleBotService(JDA discordBot, DiscordUserRepository userRepo,
                            ProblemRepository problemRepo, AttemptRepository attemptRepo) {
        this.discordBot = discordBot;
        this.userRepo = userRepo;
        this.problemRepo = problemRepo;
        this.attemptRepo = attemptRepo;
    }

    @Transactional
    public void indexHistory() throws IOException {
        Map<String, String> wordleIndex = problemSolutions = WordleUtil.getAnswerIndex();
        int startingProblemId = 200;
        for (String problemId : wordleIndex.keySet()) {
            // only need to worry about problems we haven't indexed yet
            if (!problemRepo.existsById(problemId)) {
                startingProblemId = Integer.parseInt(problemId);
                break;
            }
        }
        // if the last indexed problem falls outside of the range of messages in #wordle, index #spam too
        // otherwise just index #wordle
        if (startingProblemId < WordleUtil.FIRST_WORDLE_CHANNEL_PROBLEM_ID) {
            indexSpamHistory();
            indexWordleHistory();
        } else {
            indexWordleHistory();
        }
    }

    @Transactional
    public void updateProblemSolutions() throws IOException {
        List<Problem> solutionlessProblems = problemRepo.findBySolutionIsNull();
        if (!solutionlessProblems.isEmpty()) {
            problemSolutions = WordleUtil.getAnswerIndex();
            for (Problem problem : solutionlessProblems) {
                String problemId = problem.getId();
                if (problemSolutions.containsKey(problemId)) {
                    problem.setSolution(problemSolutions.get(problemId));
                } else {
                    logger.info("No solution found for problem " + problemId);
                }
            }
        }

    }

    @Transactional
    private void saveUserAttempt(Attempt attempt, String userId) {
        Optional<DiscordUser> userResult;
        DiscordUser user;
        if ((userResult = userRepo.findById(userId)).isPresent()) {
            user = userResult.get();
            user.addAttempt(attempt);
        } else {
            user = new DiscordUser(userId);
            user = userRepo.save(user);
            user.addAttempt(attempt);
        }
    }

    @Transactional
    private void saveProblemAttempt(Attempt attempt, String problemId) {
        Optional<Problem> problemResult;
        Problem problem;
        if ((problemResult = problemRepo.findById(problemId)).isPresent()) {
            problem = problemResult.get();
            problem.addAttempt(attempt);
        } else {
            problem = new Problem(problemId);
            problem = problemRepo.save(problem);
            problem.addAttempt(attempt);
        }
        // if the problem has no solution and we've indexed the available solutions already, set the solution
        if (problem.getSolution() == null) {
            if (problemSolutions != null && problemSolutions.containsKey(problemId)) {
                String solution = problemSolutions.get(problemId);
                problem.setSolution(solution);
            }
        }
    }

    @Transactional
    public void indexNewDiscordMessage(Message message, String problemId) {
        String userId = message.getAuthor().getName();
        String messageContent = message.getContentRaw();
        List<List<String>> wordleGuesses = WordleUtil.getWordleGuesses(messageContent);
        int numGuesses = wordleGuesses.size();
        double score = WordleUtil.getWordleScore(wordleGuesses);
        Attempt attempt = new Attempt(numGuesses, score);
        saveUserAttempt(attempt, userId);
        saveProblemAttempt(attempt, problemId);
    }

    @Transactional
    private void indexPastDiscordMessage(Message message) {
        String problemId;
        String userId = message.getAuthor().getName();
        String messageContent = message.getContentRaw();
        if (!messageContent.isEmpty()) {
            if ((problemId = WordleUtil.getProblemId(messageContent)) != null) {
                List<List<String>> wordleGuesses = WordleUtil.getWordleGuesses(messageContent);
                int numGuesses = wordleGuesses.size();
                double score = WordleUtil.getWordleScore(wordleGuesses);
                Attempt attempt = new Attempt(numGuesses, score);
                saveUserAttempt(attempt, userId);
                saveProblemAttempt(attempt, problemId);
            }
        }
    }

    @Transactional
    private void indexSpamHistory() {
        TextChannel spam = discordBot.getTextChannelById(DiscordConstants.SPAM_CHANNEL_ID);
        if (spam != null) {
            spam.getHistoryAfter(DiscordConstants.FIRST_SPAM_WORDLE_BLOCK_MESSAGE_ID, 100).complete()
                    .getRetrievedHistory().forEach(this::indexPastDiscordMessage);
            spam.getHistoryAfter(DiscordConstants.SECOND_SPAM_WORDLE_BLOCK_MESSAGE_ID, 15).complete()
                    .getRetrievedHistory().forEach(this::indexPastDiscordMessage);
        }
    }

    @Transactional
    private void indexWordleHistory() {
        TextChannel wordle = discordBot.getTextChannelById(DiscordConstants.WORDLE_CHANNEL_ID);
        if (wordle != null) {
            wordle.getIterableHistory().forEach(this::indexPastDiscordMessage);
        }
    }
}
