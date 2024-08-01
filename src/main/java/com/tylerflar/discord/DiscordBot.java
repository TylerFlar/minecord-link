package com.tylerflar.discord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.publisher.Mono;

public class DiscordBot {
    private final JavaPlugin plugin;
    private GatewayDiscordClient client;

    public DiscordBot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        String token = plugin.getConfig().getString("discord.token");
        if (token == null || token.equals("YOUR_BOT_DISCORD_TOKEN")) {
            plugin.getLogger().severe("Discord token is not set. Please set it in the config.yml file!");
            return;
        }

        client = DiscordClientBuilder.create(token).build().login().block();

        if (client != null) {
            client.on(ReadyEvent.class).flatMap(event -> Mono.fromRunnable(() -> 
                plugin.getLogger().info("Logged in as " + event.getSelf().getUsername())))
            .subscribe();
            client.onDisconnect().subscribe();
        } else {
            plugin.getLogger().severe("Failed to login to Discord!");
        }
    }

    public void stop() {
        if (client != null) {
            client.logout().block();
        }
    }

    public void restart() {
        stop();
        start();
    }
}