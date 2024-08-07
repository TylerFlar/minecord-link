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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
                    .setActivity(Activity.playing("Minecraft"))
                    .addEventListeners(this, commandManager)
                    .build();
            jda.awaitReady();

            // Register all commands globally
            registerCommands();
            commandManager.updateGlobalCommands(jda);

            plugin.getLogger().info("Discord bot started and commands registered successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to start Discord bot: " + e.getMessage());
        }
    }

    private void registerCommands() {
        commandManager.clearCommands();
        commandManager.registerCommand(new PingCommand());
        commandManager.registerCommand(new AdminCommand(plugin));
        commandManager.registerCommand(new SetupCommand(plugin));
        commandManager.registerCommand(new PlayersCommand());
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
        if (configChannelId != null && !configChannelId.isEmpty()
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