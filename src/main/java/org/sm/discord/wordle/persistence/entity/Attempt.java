package org.sm.discord.wordle.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "attempt")
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attempt_seq")
    @SequenceGenerator(name = "attempt_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private DiscordUser user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "num_guesses")
    private Integer numGuesses;

    @Column(name = "score")
    private Double score;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiscordUser getUser() {
        return user;
    }

    public void setUser(DiscordUser user) {
        this.user = user;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Integer getNumGuesses() {
        return numGuesses;
    }

    public void setNumGuesses(Integer guesses) {
        this.numGuesses = guesses;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}