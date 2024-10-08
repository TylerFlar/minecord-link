package com.tylerflar.listeners;

import com.tylerflar.MineCordLink;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.Color;
import java.time.Instant;
import java.util.UUID;

public class JoinListener implements Listener {
    private final MineCordLink plugin;

    public JoinListener(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isListenerEnabled("join")) {
            return;
        }
        String playerName = event.getPlayer().getName();
        String avatarUrl = "https://mc-heads.net/avatar/" + playerName;

        UUID playerUUID = event.getPlayer().getUniqueId();
        String discordId = plugin.getLinkedDiscordId(playerUUID);
        String discordMention = discordId != null ? "<@" + discordId + ">" : "";
        String displayName = playerName + (discordMention.isEmpty() ? "" : " (" + discordMention + ")");

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(Color.decode("#2ECC71").getRGB())
                .setAuthor(new WebhookEmbed.EmbedAuthor(playerName, avatarUrl, null))
                .setDescription(displayName + " has joined the server!")
                .setTimestamp(Instant.now())
                .build();

        plugin.getWebhookManager().sendMessage(
                null, // No content, using embed instead
                null, // No username for webhook
                null, // No avatar URL for webhook
                embed);
    }
}