package com.tylerflar.discord;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

import com.tylerflar.discord.commands.CommandManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

import com.tylerflar.MineCordLink;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;

public class DiscordBot extends ListenerAdapter {
    private final MineCordLink plugin;
    private JDA jda;
    private final CommandManager commandManager;
    private String botAvatarUrl;
    private String botUsername;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public DiscordBot(MineCordLink plugin) {
        this.plugin = plugin;
        this.commandManager = new CommandManager(plugin);
    }

    public void start() {
        String token = plugin.getConfig().getString("discord.token");
        if (token == null || token.equals("YOUR_BOT_DISCORD_TOKEN")) {
            plugin.getLogger().severe("Discord token is not set. Please set it in the config.yml file!");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_WEBHOOKS,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(null)
                    .addEventListeners(this, commandManager)
                    .build();
            jda.awaitReady();

            commandManager.updateGlobalCommands(jda);

            plugin.getLogger().info("Discord bot started and commands registered successfully!");
            startActivityUpdater();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to start Discord bot: " + e.getMessage());
        }
    }

    private void startActivityUpdater() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (jda != null) {
                String activity = getRandomActivity();
                jda.getPresence().setActivity(Activity.playing(activity));
            }
        }, 0L, 20 * 60L); // Update every 1 minute (20 ticks * 60)
    }

    private String getRandomActivity() {
        int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
        int maxPlayers = plugin.getServer().getMaxPlayers();
        String serverVersion = plugin.getServer().getVersion();
        String worldName = plugin.getServer().getWorlds().get(0).getName();

        List<String> activities = Arrays.asList(
            "with " + onlinePlayers + "/" + maxPlayers + " players",
            "on " + serverVersion,
            "in " + worldName,
            "Minecraft",
            "Block Party",
            "Survival Mode",
            "Creative Mode",
            "Hardcore Mode",
            "Crafting tables",
            "Mining diamonds",
            "Slaying dragons",
            "Building castles",
            "Exploring caves",
            "Farming wheat",
            "Taming wolves",
            "Enchanting tools",
            "Brewing potions",
            "Nether adventures",
            "End expeditions"
        );

        return activities.get(new Random().nextInt(activities.size()));
    }

    public void stop() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public void restart() {
        stop();
        start();
    }

    @Override
    public void onReady(ReadyEvent event) {
        plugin.getLogger().info("Logged in as " + event.getJDA().getSelfUser().getName());
        this.botAvatarUrl = event.getJDA().getSelfUser().getEffectiveAvatarUrl();
        this.botUsername = event.getJDA().getSelfUser().getName();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String configChannelId = plugin.getConfig().getString("discord.channel_id");
        boolean crossChatEnabled = plugin.getConfig().getBoolean("crosschat_enabled", true);

        if (crossChatEnabled && configChannelId != null
                && event.getChannel().getId().equals(configChannelId)) {
            if (!event.getMessage().isWebhookMessage()) {
                StringBuilder attachmentInfo = new StringBuilder();
                boolean isEmpty = event.getMessage().getContentDisplay().isEmpty();

                // Handle embeds
                if (!event.getMessage().getEmbeds().isEmpty()) {
                    attachmentInfo.append(isEmpty ? "" : " ").append("[embed]");
                }

                // Handle stickers
                if (!event.getMessage().getStickers().isEmpty()) {
                    attachmentInfo.append(isEmpty ? "" : " ").append("[sticker]");
                }

                // Handle attachments
                for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                    String shortUrl = shortenUrl(attachment.getUrl());
                    attachmentInfo.append(isEmpty ? "" : " ")
                            .append(ChatColor.BLUE).append("[").append(attachment.getFileName()).append("]")
                            .append(ChatColor.GRAY).append(" (").append(shortUrl).append(")");
                }

                String discordUsername = event.getAuthor().getName();
                String minecraftUsername = getLinkedMinecraftUsername(event.getAuthor().getId());
                String displayName = discordUsername + (minecraftUsername != null ? " (" + minecraftUsername + ")" : "");

                String message = ChatColor.DARK_AQUA + displayName + ": " +
                        ChatColor.WHITE + event.getMessage().getContentDisplay() +
                        attachmentInfo.toString();

                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.broadcastMessage(message));
            }
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        String configChannelId = plugin.getConfig().getString("discord.channel_id");
        boolean crossChatEnabled = plugin.getConfig().getBoolean("crosschat_enabled", true);

        if (crossChatEnabled && configChannelId != null
                && event.getChannel().getId().equals(configChannelId)) {
            if (!event.getMessage().isWebhookMessage()) {
                String discordUsername = event.getAuthor().getName();
                String minecraftUsername = getLinkedMinecraftUsername(event.getAuthor().getId());
                String displayName = discordUsername + (minecraftUsername != null ? " (" + minecraftUsername + ")" : "");

                String editedMessage = ChatColor.DARK_AQUA + displayName + " edited: " +
                        ChatColor.WHITE + event.getMessage().getContentDisplay();

                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.broadcastMessage(editedMessage));
            }
        }
    }

    private String shortenUrl(String longUrl) {
        try {
            String apiUrl = "https://tinyurl.com/api-create.php?url=" + longUrl;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            plugin.getLogger().warning("Failed to shorten URL: " + e.getMessage());
            return longUrl; // Return the original URL if shortening fails
        }
    }

    private String getLinkedMinecraftUsername(String discordId) {
        UUID minecraftUUID = plugin.getLinkedMinecraftUUID(discordId);
        if (minecraftUUID != null) {
            return Bukkit.getOfflinePlayer(minecraftUUID).getName();
        }
        return null;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JDA getJDA() {
        return jda;
    }

    public String getBotAvatarUrl() {
        return botAvatarUrl;
    }

    public String getBotUsername() {
        return botUsername;
    }
}