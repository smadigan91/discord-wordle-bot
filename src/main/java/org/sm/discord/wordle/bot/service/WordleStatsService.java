package org.sm.discord.wordle.bot.service;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.persistence.repository.AttemptRepository;
import org.sm.discord.wordle.persistence.repository.DiscordUserRepository;
import org.sm.discord.wordle.persistence.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("wordleStatsService")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WordleStatsService {

    private static final Logger logger = LoggerFactory.getLogger(WordleStatsService.class);

    private final DiscordUserRepository userRepo;

    private final ProblemRepository problemRepo;

    private final AttemptRepository attemptRepo;

    private Map<String, String> problemSolutions;

    @Autowired
    public WordleStatsService(JDA discordBot, DiscordUserRepository userRepo,
                              ProblemRepository problemRepo, AttemptRepository attemptRepo) {
        this.userRepo = userRepo;
        this.problemRepo = problemRepo;
        this.attemptRepo = attemptRepo;
    }

    // leaderboards and stat-based commands
}
