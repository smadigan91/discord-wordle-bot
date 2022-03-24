package org.sm.discord.wordle.bot.pojo;

public class WordleRow {

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
