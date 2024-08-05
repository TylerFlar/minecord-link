package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class AdminCommand implements Command {
    private final JavaPlugin plugin;
    private final List<String> authorizedUsers;

    public AdminCommand(JavaPlugin plugin, List<String> authorizedUsers) {
        this.plugin = plugin;
        this.authorizedUsers = authorizedUsers;
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getDescription() {
        return "Run an admin command on the Minecraft server";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(
            new OptionData(OptionType.STRING, "command", "The command to execute", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        if (!authorizedUsers.contains(userId)) {
            event.reply("You are not authorized to use this command.").setEphemeral(true).queue();
            return;
        }

        OptionMapping commandOption = event.getOption("command");
        if (commandOption == null) {
            event.reply("Please provide a command to execute.").setEphemeral(true).queue();
            return;
        }

        String command = commandOption.getAsString();
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

        event.reply("Command executed: " + command).setEphemeral(true).queue();
    }
}