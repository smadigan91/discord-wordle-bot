package org.sm.discord.wordle.app;

import org.sm.discord.wordle.bot.service.WordleBotService;
import org.sm.discord.wordle.bot.util.WordleUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Map;

@SpringBootApplication(scanBasePackages = "org.sm.discord.wordle")
@EnableJpaRepositories("org.sm.discord.wordle.persistence.repository")
@EntityScan("org.sm.discord.wordle.persistence.entity")
public class DiscordWordleBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordWordleBotApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx, WordleBotService bot) {
        return args -> {
            Map<String, String> pastSolutions = WordleUtil.getAnswerIndex();
            pastSolutions.forEach((k, v) -> {
                System.out.println("" + k + ":" + v);
            });
        };
    }
}
