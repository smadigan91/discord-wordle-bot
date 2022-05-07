package org.sm.discord.wordle.bot.service;

import com.inamik.text.tables.Cell;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.listener.WordleCommandListener;
import org.sm.discord.wordle.persistence.entity.Attempt;
import org.sm.discord.wordle.persistence.entity.DiscordUser;
import org.sm.discord.wordle.persistence.repository.AttemptRepository;
import org.sm.discord.wordle.persistence.repository.DiscordUserRepository;
import org.sm.discord.wordle.persistence.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.inamik.text.tables.Cell.Functions.BOTTOM_ALIGN;
import static com.inamik.text.tables.Cell.Functions.HORIZONTAL_CENTER;

@Service("wordleStatsService")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WordleStatsService {

    private static final Logger logger = LoggerFactory.getLogger(WordleStatsService.class);

    private static final String TOTAL = "Total";
    private static final String AVERAGE = "Avg";
    private static final String PROBLEM = "Problem";
    private static final String USER = "User";
    private static final String ATTEMPTS = "Attempts";
    private static final String GUESSES = "Guesses";
    private static final String SCORE = "Score";
    private static final String NUMBER = "Number";
    private static final String SOLUTION = "Solution";
    private static final int TABLE_HEIGHT = 2;
    private static final int TABLE_WIDTH = 10;

    private final JDA discordBot;

    private final DiscordUserRepository userRepo;

    private final ProblemRepository problemRepo;

    private final AttemptRepository attemptRepo;

    private Map<String, String> problemSolutions;

    @Autowired
    public WordleStatsService(JDA discordBot, DiscordUserRepository userRepo,
                              ProblemRepository problemRepo, AttemptRepository attemptRepo) {
        this.discordBot = discordBot;
        this.userRepo = userRepo;
        this.problemRepo = problemRepo;
        this.attemptRepo = attemptRepo;
        discordBot.addEventListener(new WordleCommandListener(this));
    }

    public int getLongestUserIdLength(List<DiscordUser> users) {
        int topLength = 0;
        for (DiscordUser user : users) {
            int userIdLength = user.getId().length();
            if (userIdLength > topLength) {
                topLength = userIdLength;
            }
        }
        return topLength;
    }

    public String getEmbeddableString(SimpleTable table) {
        StringBuilder builder = new StringBuilder();
        GridTable grid = table.toGrid();
        grid = Border.of(Border.Chars.of('+', '-', '|')).apply(grid);
        grid.apply(Cell.Functions.TOP_ALIGN).apply(Cell.Functions.LEFT_ALIGN);
        builder.append("```");
        for (String line : grid.toCell()) {
            builder.append(line).append("\n");
        }
        builder.append("```");
        return builder.toString();
    }

    public void addHeaderRow(SimpleTable table, Integer width, String... headers) {
        table.nextCell();
        for (String header : headers) {
            table.addLine(header);
        }
        table.applyToCell(BOTTOM_ALIGN.withHeight(TABLE_HEIGHT)).applyToCell(HORIZONTAL_CENTER.withWidth(width != null ? width : TABLE_WIDTH));
    }

    public void addDataValue(SimpleTable table, String value) {
        table.nextCell().addLine(value).applyToCell(HORIZONTAL_CENTER.withWidth(TABLE_WIDTH));
    }

    // leaderboards and stat-based commands
    @Transactional(readOnly = true)
    public String getUserLeaderboard() {
        List<DiscordUser> topUsers = userRepo.findAllByOrderByTotalScoreDesc();
        int longestUserIdLength = getLongestUserIdLength(topUsers);
        SimpleTable table = new SimpleTable().nextRow();
        addHeaderRow(table, longestUserIdLength, USER);
        addHeaderRow(table, null, TOTAL, ATTEMPTS);
        addHeaderRow(table, null, TOTAL, SCORE);
        addHeaderRow(table, null, AVERAGE, GUESSES);
        addHeaderRow(table, null, AVERAGE, SCORE);
        for (DiscordUser topUser : topUsers) {
            table.nextRow().nextCell().addLine(topUser.getId());
            addDataValue(table, String.valueOf(topUser.getTotalAttempts()));
            addDataValue(table, String.valueOf(topUser.getTotalScore()));
            addDataValue(table, String.valueOf(topUser.getAverageGuesses()));
            addDataValue(table, String.valueOf(topUser.getAverageScore()));
        }
        return getEmbeddableString(table);
    }

    @Transactional(readOnly = true)
    public String getUserScore(String userId, String problemId) {
        Optional<Attempt> attemptResult;
        if ((attemptResult = attemptRepo.findByUserIdAndProblemId(userId, problemId)).isPresent()) {
            Attempt attempt = attemptResult.get();
            return "Your score on problem #" + problemId + " was " + attempt.getScore();
        } else {
            return "No score recorded for user " + userId + " and problem " + problemId;
        }
    }

    //getTopProblems (easiest)
    //getBottomProblems (hardest)
    //getTopAttempts (highest score)
    //getBottomAttempts (worst score)
}
