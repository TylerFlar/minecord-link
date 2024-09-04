package com.tylerflar.discord.commands;

import com.tylerflar.MineCordLink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Color;

public class CommandManager extends ListenerAdapter {
    private final MineCordLink plugin;
    private final List<Command> commands = new ArrayList<>();

    public CommandManager(MineCordLink plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        registerCommand(new SetupCommand(plugin));
        registerCommand(new LinkMinecraftCommand(plugin));
        registerCommand(new PingCommand());
        registerCommand(new PlayersCommand());
        registerCommand(new CrossChatToggleCommand(plugin));
        registerCommand(new AdminCommand(plugin));
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
                MessageEmbed errorResponse = new EmbedBuilder()
                        .setTitle("Setup Required")
                        .setDescription(
                                "The bot has not been set up yet. Please use the `/setup` command to configure the bot.")
                        .setColor(Color.decode("#F1C40F").getRGB())
                        .build();
                event.replyEmbeds(errorResponse).setEphemeral(true).queue();
                return;
            }
        } else if (!serverId.equals(configServerId) || !channelId.equals(configChannelId)) {
            if (!event.getName().equals("setup")) {
                String correctServerLink = "https://discord.com/channels/" + configServerId + "/" + configChannelId;
                MessageEmbed errorResponse = new EmbedBuilder()
                        .setTitle("Wrong Channel")
                        .setDescription("This command can only be used in the configured server and channel.\n" +
                                "Please use this command in the correct channel: [Click here](" + correctServerLink
                                + ")")
                        .setColor(Color.decode("#9B59B6").getRGB())
                        .build();
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