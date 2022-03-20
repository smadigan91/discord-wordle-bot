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
    @JoinColumn(name = "username")
    private DiscordUser user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "guesses", length = 100)
    private String guesses;

    @Column(name = "score")
    private Integer score;

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

    public String getGuesses() {
        return guesses;
    }

    public void setGuesses(String guesses) {
        this.guesses = guesses;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}