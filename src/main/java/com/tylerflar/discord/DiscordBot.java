package com.tylerflar.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import com.tylerflar.discord.commands.AdminCommand;
import com.tylerflar.discord.commands.CommandManager;
import com.tylerflar.discord.commands.PingCommand;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class DiscordBot extends ListenerAdapter {
    private final JavaPlugin plugin;
    private JDA jda;
    private final CommandManager commandManager;
    private final List<String> authorizedUsers;

    public DiscordBot(JavaPlugin plugin, List<String> authorizedUsers) {
        this.plugin = plugin;
        this.authorizedUsers = authorizedUsers;
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

            // Register all commands here
            registerCommands();
            commandManager.registerCommands(jda);

            plugin.getLogger().info("Discord bot started and commands registered successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to start Discord bot: " + e.getMessage());
        }
    }

    private void registerCommands() {
        commandManager.registerCommand(new PingCommand());
        commandManager.registerCommand(new AdminCommand(plugin, authorizedUsers));
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
}