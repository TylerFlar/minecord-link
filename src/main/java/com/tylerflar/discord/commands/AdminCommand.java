package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.entities.MessageEmbed;

import com.tylerflar.MineCordLink;
import com.tylerflar.discord.utils.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class AdminCommand implements Command {
    private final JavaPlugin plugin;

    public AdminCommand(JavaPlugin plugin) {
        this.plugin = plugin;
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
        List<String> authorizedUsers = ((MineCordLink) plugin).getAuthorizedUsers();
        if (!authorizedUsers.contains(userId)) {
            MessageEmbed errorResponse = MessageFormatter.formatError("Unauthorized", "You are not authorized to use this command.");
            event.replyEmbeds(errorResponse).setEphemeral(true).queue();
            return;
        }

        OptionMapping commandOption = event.getOption("command");
        if (commandOption == null) {
            MessageEmbed errorResponse = MessageFormatter.formatError("Invalid Input", "Please provide a command to execute.");
            event.replyEmbeds(errorResponse).setEphemeral(true).queue();
            return;
        }

        String command = commandOption.getAsString();
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

        MessageEmbed successResponse = MessageFormatter.formatSuccess("Command Executed", "The following command was executed: `" + command + "`");
        event.replyEmbeds(successResponse).setEphemeral(true).queue();
    }
}