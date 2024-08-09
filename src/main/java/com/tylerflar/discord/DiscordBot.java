package com.tylerflar.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import com.tylerflar.discord.commands.AdminCommand;
import com.tylerflar.discord.commands.CommandManager;
import com.tylerflar.discord.commands.PingCommand;
import com.tylerflar.discord.commands.SetupCommand;
import com.tylerflar.discord.commands.PlayersCommand;
import com.tylerflar.discord.commands.CrossChatToggleCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DiscordBot extends ListenerAdapter {
    private final JavaPlugin plugin;
    private JDA jda;
    private final CommandManager commandManager;

    public DiscordBot(JavaPlugin plugin) {
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

            // Register all commands globally
            registerCommands();
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

    private void registerCommands() {
        commandManager.clearCommands();
        commandManager.registerCommand(new PingCommand());
        commandManager.registerCommand(new AdminCommand(plugin));
        commandManager.registerCommand(new SetupCommand(plugin));
        commandManager.registerCommand(new PlayersCommand());
        commandManager.registerCommand(new CrossChatToggleCommand(plugin));
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
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String configChannelId = plugin.getConfig().getString("discord.channel_id");
        boolean crossChatEnabled = plugin.getConfig().getBoolean("crosschat_enabled", true);

        if (crossChatEnabled && configChannelId != null && !configChannelId.isEmpty()
                && event.getChannel().getId().equals(configChannelId)) {
            if (!event.getMessage().isWebhookMessage()) {
                StringBuilder attachmentInfo = new StringBuilder();
                boolean isEmpty = event.getMessage().getContentDisplay().isEmpty();

                // Handle embeds
                if (!event.getMessage().getEmbeds().isEmpty()) {
                    attachmentInfo.append(isEmpty ? "[Attached " : "\n[Sent ").append("an embed]");
                }

                // Handle stickers
                if (!event.getMessage().getStickers().isEmpty()) {
                    attachmentInfo.append(isEmpty ? "[Attached " : "\n[Sent ").append("a sticker]");
                }

                // Handle attachments
                for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                    attachmentInfo.append(isEmpty ? "[Attached " : "\n[Sent ")
                            .append(attachment.getContentType()).append("]");
                }

                String message = ChatColor.DARK_AQUA + event.getAuthor().getName() + ": " +
                        ChatColor.WHITE + event.getMessage().getContentDisplay() +
                        ChatColor.RED + attachmentInfo.toString();

                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.broadcastMessage(message));
            }
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JDA getJDA() {
        return jda;
    }
}