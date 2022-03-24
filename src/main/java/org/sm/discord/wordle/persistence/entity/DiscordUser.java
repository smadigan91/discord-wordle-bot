package org.sm.discord.wordle.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discord_user")
public class DiscordUser extends CalculableEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<Attempt> attempts = new ArrayList<>();

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    public void addAttempt(Attempt attempt) {
        super.addAttempt(attempt);
        this.attempts.add(attempt);
    }
}