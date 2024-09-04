package com.tylerflar.commands;

import com.tylerflar.MineCordLink;
import com.tylerflar.discord.DiscordBot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final MineCordLink plugin;
    private final DiscordBot discordBot;

    public ReloadCommand(MineCordLink plugin, DiscordBot discordBot) {
        this.plugin = plugin;
        this.discordBot = discordBot;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("minecordlink")) {
            return false;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            return false;
        }

        if (!sender.hasPermission("minecordlink.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        plugin.reloadConfig();
        discordBot.restart();
        sender.sendMessage("MineCord-Link configuration reloaded!");
        return true;
    }
}