package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import com.tylerflar.MineCordLink;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.awt.Color;

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
                new OptionData(OptionType.STRING, "command", "The command to execute", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        List<String> authorizedUsers = ((MineCordLink) plugin).getAuthorizedUsers();
        if (!authorizedUsers.contains(userId)) {
            MessageEmbed errorResponse = new EmbedBuilder()
                    .setTitle("Unauthorized")
                    .setDescription("You are not authorized to use this command.")
                    .setColor(Color.decode("#FF4C4C").getRGB())
                    .build();
            event.replyEmbeds(errorResponse).setEphemeral(true).queue();
            return;
        }

        OptionMapping commandOption = event.getOption("command");
        if (commandOption == null) {
            MessageEmbed errorResponse = new EmbedBuilder()
                    .setTitle("Invalid Input")
                    .setDescription("Please provide a command to execute.")
                    .setColor(Color.decode("#FFA500").getRGB())
                    .build();
            event.replyEmbeds(errorResponse).setEphemeral(true).queue();
            return;
        }

        String command = commandOption.getAsString();
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

        MessageEmbed successResponse = new EmbedBuilder()
                .setTitle("Command Executed")
                .setDescription("The following command was executed: `" + command + "`")
                .setColor(Color.decode("#4CAF50").getRGB())
                .build();
        event.replyEmbeds(successResponse).setEphemeral(true).queue();
    }
}