package com.tylerflar.listeners;

import com.tylerflar.MineCordLink;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import java.util.UUID;

public class ChatListener implements Listener {
    private final MineCordLink plugin;
    private boolean crossChatEnabled;

    public ChatListener(MineCordLink plugin) {
        this.plugin = plugin;
        this.crossChatEnabled = plugin.getConfig().getBoolean("crosschat_enabled", true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!crossChatEnabled) {
            return;
        }

        Player player = event.getPlayer();
        String message = ChatColor.stripColor(event.getMessage());

        // Get linked Discord ID
        UUID playerUUID = player.getUniqueId();
        String discordId = plugin.getLinkedDiscordId(playerUUID);
        String discordUsername = null;
        if (discordId != null) {
            plugin.getLogger().info("Found Discord ID for " + player.getName() + ": " + discordId);
            net.dv8tion.jda.api.entities.User discordUser = plugin.getDiscordBot().getJDA().retrieveUserById(discordId).complete();
            if (discordUser != null) {
                // Get the guild (server) where the bot is operating
                net.dv8tion.jda.api.entities.Guild guild = plugin.getDiscordBot().getJDA().getGuildById(plugin.getConfig().getString("discord.server_id"));
                if (guild != null) {
                    net.dv8tion.jda.api.entities.Member member = guild.retrieveMember(discordUser).complete();
                    if (member != null) {
                        discordUsername = member.getNickname() != null ? member.getNickname() : discordUser.getName();
                    } else {
                        discordUsername = discordUser.getName();
                    }
                } else {
                    discordUsername = discordUser.getName();
                }
                plugin.getLogger().info("Retrieved Discord username/nickname: " + discordUsername);
            } else {
                plugin.getLogger().warning("Could not retrieve Discord user for ID: " + discordId);
            }
        } else {
            plugin.getLogger().info("No linked Discord ID found for " + player.getName());
        }

        String displayName = player.getName() + (discordUsername != null ? " (" + discordUsername + ")" : "");

        String avatarUrl = "https://mc-heads.net/avatar/" + player.getName();

        plugin.getLogger().info("Sending message with displayName: " + displayName);

        plugin.getWebhookManager().sendMessage(
                message,
                displayName,
                avatarUrl,
                null // No embed
        );
    }

    public void setCrossChatEnabled(boolean enabled) {
        this.crossChatEnabled = enabled;
    }
}