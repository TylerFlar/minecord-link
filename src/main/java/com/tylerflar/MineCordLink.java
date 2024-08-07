package com.tylerflar;

import org.bukkit.plugin.java.JavaPlugin;
import com.tylerflar.discord.DiscordBot;
import com.tylerflar.discord.WebhookManager;
import com.tylerflar.discord.commands.CommandManager;
import com.tylerflar.listeners.ChatListener;
import com.tylerflar.listeners.JoinListener;
import com.tylerflar.listeners.LeaveListener;
import com.tylerflar.listeners.PlayerDeathListener;
import com.tylerflar.listeners.PlayerAdvancementListener;
import com.tylerflar.commands.ReloadCommand;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;

public class MineCordLink extends JavaPlugin {
    private DiscordBot discordBot;
    private WebhookManager webhookManager;

    @Override
    public void onEnable() {
        updateConfig();
        this.discordBot = new DiscordBot(this);
        discordBot.start();
        getCommand("minecordlink").setExecutor(new ReloadCommand(this, discordBot));
        this.webhookManager = new WebhookManager(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerAdvancementListener(this), this);
        getLogger().info("MineCordLink has been enabled!");
    }

    @Override
    public void onDisable() {
        if (discordBot != null) {
            discordBot.stop();
        }
        if (webhookManager != null) {
            webhookManager.shutdown();
        }
        getLogger().info("MineCordLink has been disabled!");
    }

    private void updateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
            return;
        }

        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("config.yml")));
        YamlConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile);

        boolean updated = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!currentConfig.contains(key) &&
                !key.startsWith("discord.server_id") &&
                !key.startsWith("discord.channel_id") &&
                !key.startsWith("discord.webhook_url")) {
                currentConfig.set(key, defaultConfig.get(key));
                updated = true;
            }
        }

        // Ensure managed fields exist but don't overwrite them
        String[] managedFields = {"server_id", "channel_id", "webhook_url"};
        for (String field : managedFields) {
            String key = "discord." + field;
            if (!currentConfig.contains(key)) {
                currentConfig.set(key, "");
                updated = true;
            }
        }

        if (updated) {
            try {
                currentConfig.save(configFile);
                getLogger().info("Config file updated with new options.");
            } catch (IOException e) {
                getLogger().severe("Could not save updated config file: " + e.getMessage());
            }
        }
    }

    public List<String> getAuthorizedUsers() {
        return getConfig().getStringList("discord.authorized_users");
    }

    public CommandManager getCommandManager() {
        return discordBot.getCommandManager();
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public WebhookManager getWebhookManager() {
        return webhookManager;
    }
}