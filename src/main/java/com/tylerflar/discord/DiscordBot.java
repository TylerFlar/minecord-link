package com.tylerflar.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordBot extends ListenerAdapter {
    private final JavaPlugin plugin;
    private JDA jda;

    public DiscordBot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        String token = plugin.getConfig().getString("discord.token");
        if (token == null || token.equals("YOUR_BOT_DISCORD_TOKEN")) {
            plugin.getLogger().severe("Discord token is not set. Please set it in the config.yml file!");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(this)
                    .build();
            jda.awaitReady();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to login to Discord: " + e.getMessage());
        }
    }

    public void stop() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public void restart() {
        stop();
        start();
    }

    @Override
    public void onReady(ReadyEvent event) {
        plugin.getLogger().info("Logged in as " + event.getJDA().getSelfUser().getName());
    }
}