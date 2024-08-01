# MineCord-Link

MineCord-Link is a Minecraft plugin that links your Minecraft server to Discord, enabling seemless communication between the two platforms.

**Note: This project is currently under development and is not yet complete.**

## Features (Planned)

- Connect your Minecraft server chat with a Discord channel.
- Send Minecraft server events (player join/leave, achievements, etc.) to Discord.
- Execute Minecraft commands from Discord
- Customizable message formats and event notifications.

## Requirements

- Java 17 or later (project is using Java 21 in the dev container)
- Spigot/Paper Minecraft server (1.13+)
- Discord Bot Token

## Installation

1. Download the latest release JAR file from the [Releases](https://github.com/TylerFlar/MineCord-Link/releases) page.
2. Place the JAR file in your Minecraft server's `plugins` folder.
3. Restart your Minecraft server.
4. Edit the `config.yml` file in the `plugins/MineCord-Link` directory to add your Discord bot token.

## Configuration

The main configuration file is located at `plugins/MineCord-Link/config.yml`. Here's an example of its contents:

```yaml
discord:
  token: YOUR_BOT_DISCORD_TOKEN
```

Replace `YOUR_BOT_DISCORD_TOKEN` with your actual Discord bot token.

## Commands

- `/minecordlink reload` - Reloads the plugin configuration (requires `minecordlink.reload` permission)

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

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[MIT License](LICENSE)

## Disclaimer

This project is not affiliated with Mojang, Minecraft, or Discord.