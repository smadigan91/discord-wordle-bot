package org.sm.discord.wordle.bot.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.exception.DiscordWordleBotRuntimeException;
import org.sm.discord.wordle.bot.listener.WordleMessageListener;
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

@Service("wordleMessageService")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WordleMessageService {

    private static final Logger logger = LoggerFactory.getLogger(WordleMessageService.class);

    private final JDA discordBot;

    private final DiscordUserRepository userRepo;

    private final ProblemRepository problemRepo;

    private final AttemptRepository attemptRepo;

    private Map<String, String> problemSolutions;

    @Autowired
    public WordleMessageService(JDA discordBot, DiscordUserRepository userRepo,
                                ProblemRepository problemRepo, AttemptRepository attemptRepo) {
        this.discordBot = discordBot;
        this.userRepo = userRepo;
        this.problemRepo = problemRepo;
        this.attemptRepo = attemptRepo;
        discordBot.addEventListener(new WordleMessageListener(this));
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
    public void updatePastProblemSolutions() throws IOException {
        List<Problem> solutionlessProblems = problemRepo.findBySolutionIsNull();
        if (!solutionlessProblems.isEmpty()) {
            problemSolutions = WordleUtil.getAnswerIndex();
            for (Problem problem : solutionlessProblems) {
                String problemId = problem.getId();
                if (problemSolutions.containsKey(problemId)) {
                    problem.setSolution(problemSolutions.get(problemId));
                    logger.info("Added solution to problem " + problemId);
                } else {
                    logger.info("No solution found for problem " + problemId);
                }
            }
        }
    }

    @Transactional
    public void checkProblemSolution(Problem problem, String problemId) {
        // if the problem has no solution and we've indexed the available solutions already, add the solution
        if (problem == null) {
            Optional<Problem> result = problemRepo.findById(problemId);
            if (result.isPresent()) {
                problem = result.get();
            } else {
                // the problem should always exist if we're calling this method, otherwise something has gone wrong
                throw new DiscordWordleBotRuntimeException("No problem found with ID " + problemId);
            }
        }
        if (problem.getSolution() == null || problem.getSolution().isEmpty()) {
            if (problemSolutions != null && problemSolutions.containsKey(problemId)) {
                String solution = problemSolutions.get(problemId);
                problem.setSolution(solution);
                logger.info("Added solution to problem " + problemId);
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
    private Problem saveProblemAttempt(Attempt attempt, String problemId) {
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
        return problem;
    }

    @Transactional
    public Double indexDiscordMessage(Message message) {
        String problemId;
        String messageContent = message.getContentRaw();
        if ((problemId = WordleUtil.getProblemId(messageContent)) != null) {
            String userId = message.getAuthor().getName();
            if (!attemptRepo.existsByUserIdAndProblemId(userId, problemId)) {
                logger.info("Processing new wordle attempt " + problemId + " for user " + userId);
                List<List<String>> wordleGuesses = WordleUtil.getWordleGuesses(messageContent);
                int numGuesses = wordleGuesses.size();
                double score = WordleUtil.getWordleScore(wordleGuesses);
                Attempt attempt = new Attempt(numGuesses, score);
                saveUserAttempt(attempt, userId);
                Problem problem = saveProblemAttempt(attempt, problemId);
                checkProblemSolution(problem, problemId);
                return score;
            } else {
                logger.info("Already processed wordle attempt " + problemId + " for user " + userId);
                checkProblemSolution(null, problemId);
            }
        }
        return null;
    }

    @Transactional
    private void indexSpamHistory() {
        TextChannel spam = discordBot.getTextChannelById(DiscordConstants.SPAM_CHANNEL_ID);
        if (spam != null) {
            spam.getHistoryAfter(DiscordConstants.FIRST_SPAM_WORDLE_BLOCK_MESSAGE_ID, 100).complete()
                    .getRetrievedHistory().forEach(this::indexDiscordMessage);
            spam.getHistoryAfter(DiscordConstants.SECOND_SPAM_WORDLE_BLOCK_MESSAGE_ID, 15).complete()
                    .getRetrievedHistory().forEach(this::indexDiscordMessage);
        }
    }

    @Transactional
    private void indexWordleHistory() {
        TextChannel wordle = discordBot.getTextChannelById(DiscordConstants.WORDLE_CHANNEL_ID);
        if (wordle != null) {
            wordle.getIterableHistory().forEach(this::indexDiscordMessage);
        }
    }
}
