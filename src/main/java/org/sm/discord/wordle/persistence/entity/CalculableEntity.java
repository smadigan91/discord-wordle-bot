package org.sm.discord.wordle.persistence.entity;

import org.apache.commons.math3.util.Precision;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

@MappedSuperclass
public class CalculableEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "total_attempts")
    private Integer totalAttempts;

    @Column(name = "total_guesses")
    private Integer totalGuesses;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "avg_guesses")
    private Double averageGuesses;

    @Column(name = "avg_score")
    private Double averageScore;

    @PrePersist
    private void calculateAverages() {
        this.averageGuesses = Precision.round((this.totalGuesses / (double) this.totalAttempts), 2);
        this.averageScore = Precision.round((this.totalScore / this.averageScore), 2);
    }

    public void addAttempt(Attempt attempt) {
        this.totalAttempts += 1;
        this.totalGuesses += attempt.getNumGuesses();
        this.totalScore = this.totalScore + attempt.getScore();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Integer getTotalGuesses() {
        return totalGuesses;
    }

    public void setTotalGuesses(Integer totalGuesses) {
        this.totalGuesses = totalGuesses;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Double getAverageGuesses() {
        return averageGuesses;
    }

    public void setAverageGuesses(Double averageGuesses) {
        this.averageGuesses = averageGuesses;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}