package com.tylerflar;

import org.bukkit.plugin.java.JavaPlugin;
import com.tylerflar.discord.DiscordBot;
import com.tylerflar.commands.ReloadCommand;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;

public class MineCordLink extends JavaPlugin {
    private DiscordBot discordBot;

    @Override
    public void onEnable() {
        updateConfig();
        List<String> authorizedUsers = getConfig().getStringList("discord.authorized_users");
        discordBot = new DiscordBot(this, authorizedUsers);
        discordBot.start();
        getCommand("minecordlink").setExecutor(new ReloadCommand(this, discordBot));
        getLogger().info("MineCordLink has been enabled!");
    }

    @Override
    public void onDisable() {
        if (discordBot != null) {
            discordBot.stop();
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
            if (!currentConfig.contains(key)) {
                currentConfig.set(key, defaultConfig.get(key));
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
}