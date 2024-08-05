package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    private final JavaPlugin plugin;
    private final List<Command> commands = new ArrayList<>();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public void registerCommands(JDA jda) {
        for (Command command : commands) {
            plugin.getLogger().info("Registering command: " + command.getName());
            jda.upsertCommand(command.getName(), command.getDescription()).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        plugin.getLogger().info("Received slash command: " + event.getName());
        for (Command command : commands) {
            if (event.getName().equals(command.getName())) {
                plugin.getLogger().info("Executing command: " + command.getName());
                command.execute(event);
                return;
            }
        }
        plugin.getLogger().warning("Command not found: " + event.getName());
    }
}