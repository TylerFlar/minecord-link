package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.plugin.java.JavaPlugin;

import com.tylerflar.discord.utils.MessageFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {
    private final JavaPlugin plugin;
    private final List<Command> commands = new ArrayList<>();
    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerCommand(new SetupCommand(plugin));
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public void clearCommands() {
        commands.clear();
    }

    public void updateGlobalCommands(JDA jda) {
        List<CommandData> commandData = commands.stream()
            .map(command -> Commands.slash(command.getName(), command.getDescription())
                .addOptions(command.getOptions()))
            .collect(Collectors.toList());
        
        jda.updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String serverId = event.getGuild().getId();
        String channelId = event.getChannel().getId();
        String configServerId = plugin.getConfig().getString("discord.server_id");
        String configChannelId = plugin.getConfig().getString("discord.channel_id");

        if (configServerId.isEmpty() || configChannelId.isEmpty()) {
            if (!event.getName().equals("setup")) {
                MessageEmbed errorResponse = MessageFormatter.formatError(
                    "Setup Required",
                    "The bot has not been set up yet. Please use the `/setup` command to configure the bot."
                );
                event.replyEmbeds(errorResponse).setEphemeral(true).queue();
                return;
            }
        } else if (!serverId.equals(configServerId) || !channelId.equals(configChannelId)) {
            if (!event.getName().equals("setup")) {
                String correctServerLink = "https://discord.com/channels/" + configServerId + "/" + configChannelId;
                MessageEmbed errorResponse = MessageFormatter.formatError(
                    "Wrong Channel",
                    "This command can only be used in the configured server and channel.\n" +
                    "Please use this command in the correct channel: [Click here](" + correctServerLink + ")"
                );
                event.replyEmbeds(errorResponse).setEphemeral(true).queue();
                return;
            }
        }

        for (Command command : commands) {
            if (event.getName().equals(command.getName())) {
                command.execute(event);
                return;
            }
        }
    }
}