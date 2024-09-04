package com.tylerflar.discord.commands;

import com.tylerflar.MineCordLink;
import com.tylerflar.commands.LinkDiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.awt.Color;

public class LinkMinecraftCommand implements Command {
    private final MineCordLink plugin;

    public LinkMinecraftCommand(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "linkminecraft";
    }

    @Override
    public String getDescription() {
        return "Link your Discord account to your Minecraft account or generate a link code";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(
                new OptionData(OptionType.STRING, "code", "The link code from Minecraft", false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getOption("code") == null) {
            // Generate a new code
            String code = generateLinkCode();
            String discordId = event.getUser().getId();
            plugin.getLinkingCodes().put(code, discordId);

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Minecraft Account Linking")
                .setDescription("Your link code is: `" + code + "`\nUse this code with the `/linkdiscord` command in Minecraft to link your accounts.")
                .setColor(Color.decode("#3498db").getRGB());
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();

            // Remove the code after 5 minutes
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getLinkingCodes().remove(code), 20 * 60 * 5);
        } else {
            String code = event.getOption("code").getAsString();
            LinkDiscordCommand linkCommand = (LinkDiscordCommand) plugin.getCommand("linkdiscord").getExecutor();
            UUID minecraftUUID = linkCommand.getLinkingCodes().remove(code);

            if (minecraftUUID != null) {
                String discordId = event.getUser().getId();
                plugin.getLinkedAccounts().put(discordId, minecraftUUID);
                plugin.saveLinkedAccounts();
                
                EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Account Linked")
                    .setDescription("Your Discord account has been successfully linked to your Minecraft account!")
                    .setColor(Color.decode("#27AE60"));
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            } else {
                EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Link Failed")
                    .setDescription("Invalid or expired link code. Please generate a new code in Minecraft.")
                    .setColor(Color.decode("#E74C3C"));
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
        }
    }

    private String generateLinkCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}