package org.sm.discord.wordle.persistence.repository;

import org.sm.discord.wordle.persistence.entity.DiscordUser;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordUserRepository extends CalculableEntityRepository<DiscordUser> {
}