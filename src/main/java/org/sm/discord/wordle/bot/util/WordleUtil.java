package org.sm.discord.wordle.bot.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sm.discord.wordle.bot.exception.InvalidWordleScoreException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordleUtil {

    public static final String BLACK_SQUARE = "⬛";
    public static final String WHITE_SQUARE = "⬜";
    public static final String GREEN_SQUARE = "🟩";
    public static final String YELLOW_SQUARE = "\uD83D\uDFE8";
    public static final Pattern WORDLE_REGEX = Pattern.compile("(?=[Ww]ordle)(?=.*/6).*");
    public static final Pattern WORDLE_EMOJI_REGEX = Pattern.compile(BLACK_SQUARE + "|" + GREEN_SQUARE + "|" + YELLOW_SQUARE);
    public static final String WORDLE_SOLUTION_URL = "https://progameguides.com/wordle/all-wordle-answers-in-2022-updated-daily/";
    public static final Integer FIRST_DISCORD_WORDLE_PROBLEM_ID = 200;
    public static final Integer FIRST_WORDLE_CHANNEL_PROBLEM_ID = 206;

    public static String getProblemId(String wordleMessage) {
        Matcher matcher = WORDLE_REGEX.matcher(wordleMessage);
        if (matcher.find()) {
            String matched = matcher.group();
            return matched.split(" ")[1];
        } else {
            return null;
        }
    }

    public static List<List<String>> getWordleGuesses(String wordleMessage) {
        Matcher matcher = WORDLE_EMOJI_REGEX.matcher(wordleMessage);
        List<List<String>> guesses = new ArrayList<>();
        List<String> row = new ArrayList<>();
        while (matcher.find()) {
            row.add(matcher.group());
            if (row.size() == 5) {
                guesses.add(row);
                row = new ArrayList<>();
            }
        }
        return guesses;
    }

    /**
     * + 2 point for black -> green
     * + 1 point for black -> yellow
     * + 0.5 point for yellow -> green
     * -1 point for no increase in score between rows
     */
    public static double getWordleScore(@NotNull List<List<String>> guesses) {
        int numGuesses = guesses.size();
        double total, bonus, penalties;
        bonus = penalties = 0;
        WordleRow previousRow = getWordleRow(guesses.remove(0));
        total = previousRow.getPoints();
        for (List<String> row : guesses) {
            WordleRow currentRow = getWordleRow(row);
            boolean higherScore = (currentRow.getPoints() > previousRow.getPoints());
            //penalty for having the same or lower score than previous row
            if (!higherScore) {
                penalties -= 1;
            }
            //if there are new yellow squares, give bonus points for each new one
            int newYellows = currentRow.getNumYellow() - previousRow.getNumYellow();
            int newGreens = currentRow.getNumGreen() - previousRow.getNumGreen();
            if (newYellows > 0) {
                bonus += newYellows;
            } else if (higherScore && newYellows < 0) {
                //if there are fewer yellows and the score is higher, some yellows must have turned into greens
                bonus += (newYellows * -0.5);
                newGreens += newYellows;
            }
            if (newGreens > 0) {
                //any remaining new greens must have been converted from greys
                bonus += (newGreens * 2);
            }
            total += currentRow.getPoints();
            previousRow = currentRow;
        }
        double totalScore = (total + bonus + penalties) / numGuesses;
        return Precision.round(totalScore, 2);
    }

    public static Map<String, String> getAnswerIndex() throws IOException {
        Document doc = Jsoup.connect(WordleUtil.WORDLE_SOLUTION_URL).get();
        Elements strongElements = doc.select("strong");
        // this will create a map of problemId:solution, trust me lol
        Map<String, String> resultMap = strongElements.stream()
                .filter(element -> (element.text().contains("- #") && element.hasParent()))
                .map(element -> {
                    element = (Element) element.parentNode();
                    return element.text().split(" ");
                })
                .collect(Collectors.toMap(k -> StringUtils.substringBetween(k[k.length - 2], "#", ":"), v -> v[v.length - 1]));
        resultMap = new TreeMap<>(resultMap);
        resultMap.keySet().removeIf(key -> Integer.parseInt(key) < FIRST_DISCORD_WORDLE_PROBLEM_ID);
        return resultMap;
    }

    /**
     * Black is 0 points
     * Yellow is 1 point
     * Green is 2 points
     */
    private static WordleRow getWordleRow(@NotNull List<String> row) {
        if (row.size() < 5) {
            throw new InvalidWordleScoreException("Malformed wordle row, unable to process score");
        }
        int points, numBlack, numYellow, numGreen;
        points = numBlack = numYellow = numGreen = 0;
        for (String square : row) {
            switch (square) {
                case BLACK_SQUARE, WHITE_SQUARE -> numBlack += 1;
                case YELLOW_SQUARE -> {
                    points += 1;
                    numYellow += 1;
                }
                case GREEN_SQUARE -> {
                    points += 2;
                    numGreen += 1;
                }
            }
        }
        return new WordleRow(numBlack, numYellow, numGreen, points);
    }

    private static class WordleRow {

        private int numBlack;
        private int numYellow;
        private int numGreen;
        private int points;

        public WordleRow(int numBlack, int numYellow, int numGreen, int points) {
            this.numBlack = numBlack;
            this.numYellow = numYellow;
            this.numGreen = numGreen;
            this.points = points;
        }

        public int getNumBlack() {
            return numBlack;
        }

        public void setNumBlack(int numBlack) {
            this.numBlack = numBlack;
        }

        public int getNumYellow() {
            return numYellow;
        }

        public void setNumYellow(int numYellow) {
            this.numYellow = numYellow;
        }

        public int getNumGreen() {
            return numGreen;
        }

        public void setNumGreen(int numGreen) {
            this.numGreen = numGreen;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }
}
