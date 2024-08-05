package com.tylerflar;

import org.bukkit.plugin.java.JavaPlugin;
import com.tylerflar.discord.DiscordBot;
import com.tylerflar.commands.ReloadCommand;

import java.util.List;

public class MineCordLink extends JavaPlugin {
    private DiscordBot discordBot;

    @Override
    public void onEnable() {
        saveDefaultConfig();
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
}