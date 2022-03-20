package org.sm.discord.wordle.persistence.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problem")
public class Problem {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "problem")
    private List<Attempt> attempts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }
}