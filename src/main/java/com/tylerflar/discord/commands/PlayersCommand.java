package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;
import java.awt.Color;

public class PlayersCommand implements Command {
    @Override
    public String getName() {
        return "players";
    }

    @Override
    public String getDescription() {
        return "Get a list of online players";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StringBuilder playerList = new StringBuilder();
        int playerCount = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerList.append(player.getName()).append("\n");
            playerCount++;
        }

        String description = playerCount > 0
                ? "There are " + playerCount + " player(s) online:\n\n" + playerList.toString()
                : "There are no players online.";

        MessageEmbed response = new EmbedBuilder()
                .setTitle("Online Players")
                .setDescription(description)
                .setColor(Color.decode("#3498DB"))
                .build();

        event.replyEmbeds(response).queue();
    }
}