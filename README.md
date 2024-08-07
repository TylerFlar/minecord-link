# MineCord-Link

MineCord-Link is a Minecraft plugin that links your Minecraft server to Discord, enabling seamless communication between the two platforms.

**Note: This project is currently under development.**

## Features

- Connect your Minecraft server chat with a Discord channel.
- Send Minecraft server events (player join/leave, deaths, achievements, etc.) to Discord.
- Execute Minecraft commands from Discord.

## Requirements

- Java 17 or later (project is using Java 21 in the dev container)
- Spigot/Paper Minecraft server (1.13+)
- Discord Bot Token

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
  # DO NOT TOUCH THE FOLLOWING VALUES MANUALLY:
  # They are managed by the plugin and will be overwritten.
  server_id: ""
  channel_id: ""
  webhook_url: ""
```

**Note:** The `server_id`, `channel_id`, and `webhook_url` fields are automatically managed by the plugin. Do not modify these values manually, as they will be overwritten when you use the `/setup` command in Discord.

## Commands

### Minecraft Commands
- `/minecordlink reload` - Reloads the plugin configuration (requires `minecordlink.reload` permission)

### Discord Commands
- `/ping` - Responds with "Pong!" to check if the bot is active
- `/admin <command>` - Executes a Minecraft server command (only for authorized users)
- `/setup` - Sets up the bot for the current server and channel

For more details on commands and permissions, see the `plugin.yml` file.

## Permissions

- `minecordlink.reload` - Allows reloading the MineCord-Link configuration (default: op)

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

[MIT License](LICENSE)

## Disclaimer

This project is not affiliated with Mojang, Minecraft, or Discord.