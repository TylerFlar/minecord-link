# Release v[VERSION]

## What's Changed

[Summarize the major changes in this release]

### New Features
- [List new features]

### Improvements
- [List improvements to existing features]

### Bug Fixes
- [List bug fixes]

## Installation

1. Download the `minecord-link-[VERSION].jar` file from the assets below.
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

## Upgrade Instructions

If you're upgrading from a previous version:

1. Stop your Minecraft server.
2. Replace the old `minecord-link-*.jar` file in your `plugins` folder with the new version.
3. Start your Minecraft server.
4. Check the console for any migration messages or warnings.

## Known Issues

[List any known issues or limitations with this release]

## Compatibility

- Minecraft Version: 1.21
- Java Version: 17+

## Support

If you encounter any issues or have questions, please [open an issue](https://github.com/TylerFlar/MineCord-Link/issues) on our GitHub repository.

## Contributors

[List contributors to this release, if applicable]

---

**Full Changelog**: https://github.com/TylerFlar/MineCord-Link/compare/v[PREVIOUS_VERSION]...v[VERSION]
