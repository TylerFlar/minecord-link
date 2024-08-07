package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.entities.MessageEmbed;
import com.tylerflar.discord.utils.MessageFormatter;
import java.util.Collections;
import java.util.List;

public class PingCommand implements Command {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Responds with Pong!";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MessageEmbed response = MessageFormatter.formatSuccess("Pong!", "The bot is responsive and working correctly.");
        event.replyEmbeds(response).queue();
    }
}