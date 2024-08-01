package com.tylerflar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import com.tylerflar.discord.DiscordBot;

public class ReloadCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DiscordBot discordBot;

    public ReloadCommand(JavaPlugin plugin, DiscordBot discordBot) {
        this.plugin = plugin;
        this.discordBot = discordBot;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("minecordlink")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("minecordlink.reload")) {
                    plugin.reloadConfig();
                    discordBot.restart();
                    sender.sendMessage("MineCord-Link configuration reloaded!");
                    return true;
                } else {
                    sender.sendMessage("You don't have permission to use this command.");
                    return true;
                }
            }
        }
        return false;
    }
}