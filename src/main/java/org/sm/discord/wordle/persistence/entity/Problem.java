package org.sm.discord.wordle.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problem")
public class Problem extends CalculableEntity {

    @OneToMany(mappedBy = "problem")
    private List<Attempt> attempts = new ArrayList<>();

    @Column(name = "solution", length = 5)
    private String solution;

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
        this.attempts.add(attempt);
    }
}