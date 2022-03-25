package org.sm.discord.wordle.persistence.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problem")
public class Problem extends CalculableEntity {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "problem", fetch = FetchType.EAGER)
    private List<Attempt> attempts = new ArrayList<>();

    @Column(name = "solution", length = 5)
    private String solution;

    public Problem() {
    }

    public Problem(String id) {
        super(id);
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void addAttempt(Attempt attempt) {
        super.addAttempt(attempt);
        if (this.attempts == null) {
            this.attempts = new ArrayList<>();
        }
        attempt.setProblem(this);
        this.attempts.add(attempt);
    }
}