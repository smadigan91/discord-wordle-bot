package org.sm.discord.wordle.configuration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.security.auth.login.LoginException;

@Configuration
public class DiscordBotConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JDA discordBot() throws LoginException, InterruptedException {
        String token = System.getenv("DISCORD_TOKEN");
        JDA bot = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES)
                .build();
        bot.awaitReady();
        return bot;
    }
}
