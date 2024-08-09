package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.plugin.java.JavaPlugin;
import com.tylerflar.MineCordLink;

import java.util.Collections;
import java.util.List;
import java.awt.Color;

public class CrossChatToggleCommand implements Command {
    private final JavaPlugin plugin;

    public CrossChatToggleCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "crosschat";
    }

    @Override
    public String getDescription() {
        return "Toggle the crosschat feature on or off";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        boolean currentState = plugin.getConfig().getBoolean("crosschat_enabled", true);
        boolean newState = !currentState;
        
        plugin.getConfig().set("crosschat_enabled", newState);
        plugin.saveConfig();

        String stateMessage = newState ? "enabled" : "disabled";
        MessageEmbed response = new EmbedBuilder()
                .setTitle("Crosschat Toggle")
                .setDescription("Crosschat has been " + stateMessage + ".")
                .setColor(newState ? Color.decode("#1ABC9C").getRGB() : Color.decode("#BDC3C7").getRGB())
                .build();

        event.replyEmbeds(response).setEphemeral(true).queue();

        // Update the ChatListener state
        ((MineCordLink) plugin).getChatListener().setCrossChatEnabled(newState);
    }
}