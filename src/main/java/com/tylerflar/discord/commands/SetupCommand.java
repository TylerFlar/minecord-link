package com.tylerflar.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.Permission;
import com.tylerflar.discord.utils.MessageFormatter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import com.tylerflar.MineCordLink;
import net.dv8tion.jda.api.entities.Webhook;

import java.util.Collections;
import java.util.List;

public class SetupCommand implements Command {
    private final JavaPlugin plugin;

    public SetupCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "Set up the bot for this server and channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Check if the user is a Discord server admin
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            MessageEmbed errorResponse = MessageFormatter.formatError("Unauthorized",
                    "You must be a Discord server admin to use this command.");
            event.replyEmbeds(errorResponse).setEphemeral(true).queue();
            return;
        }

        // Check if the user is a Minecraft server admin
        String userId = event.getUser().getId();
        List<String> authorizedUsers = ((MineCordLink) plugin).getAuthorizedUsers();
        if (!authorizedUsers.contains(userId)) {
            MessageEmbed errorResponse = MessageFormatter.formatError("Unauthorized",
                    "You must be a Minecraft server admin to use this command.");
            event.replyEmbeds(errorResponse).setEphemeral(true).queue();
            return;
        }

        String serverId = event.getGuild().getId();
        String channelId = event.getChannel().getId();

        // Get the existing configuration
        FileConfiguration config = plugin.getConfig();
        String existingChannelId = config.getString("discord.channel_id", "");

        // Delete existing MineCord-Link webhooks in the configured channel
        if (!existingChannelId.isEmpty()) {
            net.dv8tion.jda.api.entities.channel.concrete.TextChannel existingChannel = event.getJDA().getTextChannelById(existingChannelId);
            if (existingChannel != null) {
                existingChannel.retrieveWebhooks().queue(webhooks -> {
                    for (Webhook webhook : webhooks) {
                        if (webhook.getName().equals("MineCord-Link")) {
                            webhook.delete().queue();
                        }
                    }
                });
            } else {
                plugin.getLogger().warning("Previously configured channel not found. Skipping webhook deletion.");
            }
        }

        // Check for existing MineCord-Link webhook in the current channel
        event.getGuildChannel().asTextChannel().retrieveWebhooks().queue(webhooks -> {
            for (Webhook webhook : webhooks) {
                if (webhook.getName().equals("MineCord-Link")) {
                    String webhookUrl = webhook.getUrl();

                    // Update the configuration
                    config.set("discord.server_id", serverId);
                    config.set("discord.channel_id", channelId);
                    config.set("discord.webhook_url", webhookUrl);
                    plugin.saveConfig();

                    // Update the WebhookManager
                    ((MineCordLink) plugin).getWebhookManager().updateWebhookUrl(webhookUrl);

                    MessageEmbed response = MessageFormatter.formatSuccess("Setup Complete",
                            "The bot has been set up for this server and channel.\n" +
                                    "Server ID: " + serverId + "\n" +
                                    "Channel ID: " + channelId);
                    event.replyEmbeds(response).setEphemeral(true).queue();
                    return;
                }
            }

            // Create new webhook if none exists
            event.getGuildChannel().asTextChannel().createWebhook("MineCord-Link").queue(webhook -> {
                String webhookUrl = webhook.getUrl();

                // Update the configuration
                config.set("discord.server_id", serverId);
                config.set("discord.channel_id", channelId);
                config.set("discord.webhook_url", webhookUrl);
                plugin.saveConfig();

                // Update the WebhookManager
                ((MineCordLink) plugin).getWebhookManager().updateWebhookUrl(webhookUrl);

                MessageEmbed response = MessageFormatter.formatSuccess("Setup Complete",
                        "The bot has been set up for this server and channel.\n" +
                                "Server ID: " + serverId + "\n" +
                                "Channel ID: " + channelId);
                event.replyEmbeds(response).setEphemeral(true).queue();
            });
        });
    }
}