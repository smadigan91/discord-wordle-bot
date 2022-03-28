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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Attempt> attempts = new ArrayList<>();

    public DiscordUser() {
    }

    public DiscordUser(String id) {
        super(id);
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    public void addAttempt(Attempt attempt) {
        super.addAttempt(attempt);
        if (this.attempts == null) {
            this.attempts = new ArrayList<>();
        }
        attempt.setUser(this);
        this.attempts.add(attempt);
    }
}