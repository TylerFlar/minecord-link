package com.tylerflar;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.tylerflar.discord.DiscordBot;
import com.tylerflar.discord.WebhookManager;
import com.tylerflar.discord.commands.CommandManager;
import com.tylerflar.listeners.ChatListener;
import com.tylerflar.listeners.JoinListener;
import com.tylerflar.listeners.LeaveListener;
import com.tylerflar.listeners.PlayerDeathListener;
import com.tylerflar.utils.UpdateChecker;
import com.tylerflar.listeners.PlayerAdvancementListener;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import com.tylerflar.commands.CoordsCommand;
import com.tylerflar.commands.ReloadCommand;
import com.tylerflar.commands.LinkDiscordCommand;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

import org.bukkit.configuration.file.YamlConfiguration;

public class MineCordLink extends JavaPlugin {
    private DiscordBot discordBot;
    private WebhookManager webhookManager;
    private ChatListener chatListener;
    private Map<String, UUID> linkedAccounts = new HashMap<>();
    private Map<String, String> linkingCodes = new HashMap<>();

    @Override
    public void onEnable() {
        updateConfig();
        this.discordBot = new DiscordBot(this);
        discordBot.start();
        getCommand("minecordlink").setExecutor(new ReloadCommand(this, discordBot));
        getCommand("coords").setExecutor(new CoordsCommand(this));
        getCommand("linkdiscord").setExecutor(new LinkDiscordCommand(this));
        // Wait for the bot to be ready before creating the WebhookManager
        getServer().getScheduler().runTaskLater(this, () -> {
            this.webhookManager = new WebhookManager(this, discordBot.getBotAvatarUrl(), discordBot.getBotUsername());
            this.chatListener = new ChatListener(this);
            getServer().getPluginManager().registerEvents(chatListener, this);
            getServer().getPluginManager().registerEvents(new JoinListener(this), this);
            getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
            getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
            getServer().getPluginManager().registerEvents(new PlayerAdvancementListener(this), this);
        }, 20L); // Wait 1 second (20 ticks) for the bot to be ready

        // Schedule the server start message to be sent after a short delay
        getServer().getScheduler().runTaskLater(this, () -> {
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setColor(Color.decode("#2980B9").getRGB())
                    .setDescription("Server has started and is ready to accept connections!")
                    .setTimestamp(Instant.now())
                    .build();

            webhookManager.sendMessage(
                    null, // No content, using embed instead
                    null, // No username for webhook
                    null, // No avatar URL for webhook
                    embed);
        }, 20L); // 20 ticks = 1 second delay

        // Check for updates
        if (getConfig().getBoolean("auto_update_check", true)) {
            new UpdateChecker(this).checkForUpdates();
        } else {
            getLogger().info("Automatic update checking is disabled.");
        }

        loadLinkedAccounts();

        getLogger().info("MineCordLink has been enabled!");
    }

    @Override
    public void onDisable() {
        if (discordBot != null) {
            discordBot.stop();
        }
        if (webhookManager != null) {
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setColor(Color.decode("#7F8C8D").getRGB())
                    .setDescription("Server has stopped.")
                    .setTimestamp(Instant.now())
                    .build();

            webhookManager.sendMessage(
                    null, // No content, using embed instead
                    null, // No username for webhook
                    null, // No avatar URL for webhook
                    embed);
            webhookManager.shutdown();
        }
        saveLinkedAccounts();
        getLogger().info("MineCordLink has been disabled!");
    }

    private void updateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
            return;
        }

        YamlConfiguration defaultConfig = YamlConfiguration
                .loadConfiguration(new InputStreamReader(getResource("config.yml")));
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
        String[] managedFields = { "server_id", "channel_id", "webhook_url" };
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

    public ChatListener getChatListener() {
        return chatListener;
    }

    public void saveLinkedAccounts() {
        File file = new File(getDataFolder(), "linked_accounts.json");
        try (FileWriter writer = new FileWriter(file)) {
            new JSONObject(linkedAccounts).write(writer);
        } catch (IOException e) {
            getLogger().severe("Failed to save linked accounts: " + e.getMessage());
        }
    }

    public void loadLinkedAccounts() {
        File file = new File(getDataFolder(), "linked_accounts.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JSONObject json = new JSONObject(new JSONTokener(reader));
                for (String key : json.keySet()) {
                    linkedAccounts.put(key, UUID.fromString(json.getString(key)));
                }
            } catch (IOException e) {
                getLogger().severe("Failed to load linked accounts: " + e.getMessage());
            }
        }
    }

    public Map<String, UUID> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void linkAccounts(String discordId, UUID minecraftUUID) {
        linkedAccounts.put(discordId, minecraftUUID);
        saveLinkedAccounts();
    }

    public void unlinkAccounts(String discordId) {
        linkedAccounts.remove(discordId);
        saveLinkedAccounts();
    }

    public UUID getLinkedMinecraftUUID(String discordId) {
        return linkedAccounts.get(discordId);
    }

    public String getLinkedDiscordId(UUID minecraftUUID) {
        for (Map.Entry<String, UUID> entry : linkedAccounts.entrySet()) {
            if (entry.getValue().equals(minecraftUUID)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<String, String> getLinkingCodes() {
        return linkingCodes;
    }
}