package com.tylerflar.listeners;

import com.tylerflar.MineCordLink;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.Color;
import java.time.Instant;

public class LeaveListener implements Listener {
    private final MineCordLink plugin;

    public LeaveListener(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        String avatarUrl = "https://mc-heads.net/avatar/" + playerName;
        
        WebhookEmbed embed = new WebhookEmbedBuilder()
            .setColor(Color.decode("#FFA500").getRGB())
            .setAuthor(new WebhookEmbed.EmbedAuthor(playerName, avatarUrl, null))
            .setDescription(playerName + " has left the server!")
            .setTimestamp(Instant.now())
            .build();
        
        plugin.getWebhookManager().sendMessage(
            null,  // No content, using embed instead
            "Server",
            null,  // No avatar URL for webhook
            embed
        );
    }
}