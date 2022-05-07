package org.sm.discord.wordle.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sm.discord.wordle.bot.service.WordleMessageService;
import org.sm.discord.wordle.bot.service.WordleStatsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.sm.discord.wordle")
@EnableJpaRepositories("org.sm.discord.wordle.persistence.repository")
@EntityScan("org.sm.discord.wordle.persistence.entity")
public class DiscordWordleBotApplication {

    private static final Logger logger = LoggerFactory.getLogger(DiscordWordleBotApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DiscordWordleBotApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx, WordleMessageService msgSvc, WordleStatsService stats) {
        return args -> {
            logger.info("Indexing history...");
            msgSvc.indexHistory();
            logger.info("Finished indexing history");
        };
    }
}
