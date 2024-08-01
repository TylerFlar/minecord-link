package com.tylerflar;

import org.bukkit.plugin.java.JavaPlugin;
import com.tylerflar.discord.DiscordBot;
import com.tylerflar.commands.ReloadCommand;

public class MineCordLink extends JavaPlugin {
    private DiscordBot discordBot;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        discordBot = new DiscordBot(this);
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