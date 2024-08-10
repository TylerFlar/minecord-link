# MineCord-Link

MineCord-Link is a Minecraft plugin that links your Minecraft server to Discord, enabling seamless communication between the two platforms.

## Features

- Connect your Minecraft server chat with a Discord channel.
- Send Minecraft server events (player join/leave, deaths, achievements, etc.) to Discord.
- Execute Minecraft commands from Discord.

## Requirements

- Java 17 or later (project is using Java 21 in the dev container)
- Spigot/Paper Minecraft server (1.13+)
- Discord Bot Token

## Discord Bot Setup

Before installing the plugin, you need to create a Discord bot:

1. Navigate to your [Discord Developer Portal Applications](https://discord.com/developers/applications) page
2. Click "New Application"
3. Enter the name "MineCord-Link" (or any name you prefer)
4. Read and agree to the Discord Developer Terms of Service and Developer Policy
5. Click "Create"
6. (Optional) Add an App Icon and click "Save Changes"
7. In the left sidebar, click "Bot"
8. Under the "Build-A-Bot" section, click "Add Bot" if it's not already added
9. Click "Reset Token"
10. Click "Yes, do it!"
11. Copy and securely save the token for later use in your plugin configuration
12. Under "Privileged Gateway Intents", enable:
    - PRESENCE INTENT
    - SERVER MEMBERS INTENT
    - MESSAGE CONTENT INTENT
13. Click "Save Changes"
14. In the left sidebar, click "Installation"
15. Uncheck "User Install" under "Installation Contexts"
16. Under "Default Install Settings Guild Install", select the following:
    - In "Scopes": bot, applications.commands
    - In "Permissions":
        - View Channels
        - Send Messages
        - Embed Links
        - Attach Files
        - Read Message History
        - Add Reactions
        - Manage Webhooks
18. Open the "Install Link" (make sure it is set to "Discord Provided Link") in a new tab or window
19. Select the server you want to add the bot to and click "Authorize"
20. Complete any additional verification steps if prompted

## Installation

1. Download the latest release JAR file from the [Releases](https://github.com/TylerFlar/MineCord-Link/releases) page.
2. Place the JAR file in your Minecraft server's `plugins` folder.
3. Restart your Minecraft server.
4. Edit the `config.yml` file in the `plugins/MineCord-Link` directory to add your Discord bot token and authorized users.

## Configuration

The main configuration file is located at `plugins/MineCord-Link/config.yml`. Here's an example of its contents:

```yaml
discord:
  token: YOUR_BOT_DISCORD_TOKEN
  authorized_users:
    - "123456789012345678" # Replace with actual Discord user IDs
    - "234567890123456789"
  auto_update_check: true
  # DO NOT TOUCH THE FOLLOWING VALUES MANUALLY:
  # They are managed by the plugin and will be overwritten.
  server_id: ""
  channel_id: ""
  webhook_url: ""
```

**Note:** The `server_id`, `channel_id`, and `webhook_url` fields are automatically managed by the plugin. Do not modify these values manually, as they will be overwritten when you use the `/setup` command in Discord.

## Setup

After installing the plugin and configuring the Discord bot token, you need to run the setup command in Discord:

1. In your Discord server, go to the channel where you want to set up the MineCord-Link bot.
2. Type `/setup` and press Enter.
3. If you're authorized, the bot will confirm that it has been set up for the current server and channel.

This step is crucial as it establishes the connection between your Minecraft server and the specific Discord channel.

## Commands

### Minecraft Commands
- `/minecordlink reload` - Reloads the plugin configuration (requires `minecordlink.reload` permission)
- `/coords [location name]` - Share your current coordinates in the game and Discord

### Discord Commands
- `/ping` - Responds with "Pong!" to check if the bot is active
- `/admin <command>` - Executes a Minecraft server command (only for authorized users)
- `/setup` - Sets up the bot for the current server and channel (requires Discord Administrator permission)
- `/players` - Displays a list of online players
- `/crosschat` - Toggles the crosschat feature on or off (only for authorized users)

For more details on commands and permissions, see the `plugin.yml` file.

## Permissions

- `minecordlink.reload` - Allows reloading the MineCord-Link configuration (default: op)

## Common Issues

### Bot is online but no commands are displayed

If the bot is online in your Discord server, but you don't see any commands when typing `/`, try the following steps:

1. Kick the bot from your server.
2. Grab the "Install Link" used in step 18 of the [Discord Bot Setup](#discord-bot-setup) section.
3. Open the link in a new tab or window.
4. Select the server you want to add the bot to and click "Authorize".
5. Complete any additional verification steps if prompted.

If you encounter any other issues or need further assistance, please [open an issue](https://github.com/TylerFlar/MineCord-Link/issues) on our GitHub repository.

## Development

This project uses Maven for dependency management and building. To set up a development environment:

1. Clone the repository
2. Open the project in your preferred Java IDE
3. Run `mvn clean install` to build the project

The project includes a dev container configuration for easy setup. To use it:

1. Install Docker and VS Code with the Remote - Containers extension
2. Open the project folder in VS Code
3. When prompted, click "Reopen in Container"

The dev container includes a Spigot server for testing. You can connect to it using the following details:

- Server Address: `localhost`
- Port: 25566

Note: The dev container uses Java 21, which is the latest LTS version. This ensures compatibility with the latest Minecraft server versions.

The dev container includes a script to build the plugin, copy it to the Minecraft server, and run the server. To use it:

1. Open a terminal in the dev container
2. Run the following command:

   ```
   /usr/local/bin/build-and-run-minecraft
   ```

This script will:
1. Build the MineCord-Link plugin using Maven
2. Copy the built JAR file to the Minecraft server's plugins folder
3. Create a symlink for the config file
4. Start the Minecraft server

You can use this script to quickly test your changes in a Minecraft server environment.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[GNU Lesser General Public License v3.0](LICENSE)

## Disclaimer

This project is not affiliated with Mojang, Minecraft, or Discord.