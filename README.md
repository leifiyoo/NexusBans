# NexusBan

A powerful and modern punishment system for Minecraft Spigot servers.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spigot](https://img.shields.io/badge/Spigot-1.21.4-yellow)
![License](https://img.shields.io/badge/License-MIT-green)

## Features

- **Ban System** - Permanent and temporary bans
- **IP-Ban System** - Block players by IP address
- **Alt Detection** - Automatically detect alt accounts
- **Mute System** - Permanent and temporary mutes  
- **Kick System** - Kick players with custom reasons
- **Warning System** - Track player warnings with auto-punish
- **Punishment GUI** - Easy-to-use graphical interface
- **Ban History** - View complete punishment history
- **Staff Protection** - Prevent staff from punishing each other
- **Async File I/O** - No server lag from saving data

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/ban <player> [reason]` | Permanently ban a player | `nexusban.ban` |
| `/tempban <player> <duration> [reason]` | Temporarily ban a player | `nexusban.tempban` |
| `/unban <player>` | Unban a player | `nexusban.unban` |
| `/ipban <player\|ip> [reason]` | IP ban a player | `nexusban.ipban` |
| `/unipban <player\|ip>` | Remove an IP ban | `nexusban.unipban` |
| `/ipbanlist` | View all active IP bans | `nexusban.ipbanlist` |
| `/kick <player> [reason]` | Kick a player | `nexusban.kick` |
| `/mute <player> [reason]` | Permanently mute a player | `nexusban.mute` |
| `/tempmute <player> <duration> [reason]` | Temporarily mute a player | `nexusban.tempmute` |
| `/unmute <player>` | Unmute a player | `nexusban.unmute` |
| `/warn <player> [reason]` | Warn a player | `nexusban.warn` |
| `/history <player>` | View punishment history | `nexusban.history` |
| `/punish <player>` | Open punishment GUI | `nexusban.gui` |
| `/banlist` | View all active bans | `nexusban.banlist` |
| `/checkban <id>` | Look up a ban by ID | `nexusban.checkban` |
| `/alts <player>` | Check for alt accounts | `nexusban.alts` |

## Duration Format

- `s` - Seconds (e.g., `30s`)
- `m` - Minutes (e.g., `10m`)
- `h` - Hours (e.g., `2h`)
- `d` - Days (e.g., `7d`)
- `w` - Weeks (e.g., `2w`)
- `M` - Months (e.g., `1M`)
- `y` - Years (e.g., `1y`)

Combine them: `1d12h30m` = 1 day, 12 hours, 30 minutes

## Permissions

| Permission | Description |
|------------|-------------|
| `nexusban.*` | All permissions |
| `nexusban.ban` | Use /ban command |
| `nexusban.tempban` | Use /tempban command |
| `nexusban.unban` | Use /unban command |
| `nexusban.ipban` | Use /ipban command |
| `nexusban.unipban` | Use /unipban command |
| `nexusban.ipbanlist` | Use /ipbanlist command |
| `nexusban.kick` | Use /kick command |
| `nexusban.mute` | Use /mute command |
| `nexusban.tempmute` | Use /tempmute command |
| `nexusban.unmute` | Use /unmute command |
| `nexusban.warn` | Use /warn command |
| `nexusban.history` | Use /history command |
| `nexusban.gui` | Use /punish GUI |
| `nexusban.banlist` | Use /banlist command |
| `nexusban.checkban` | Use /checkban command |
| `nexusban.alts` | Use /alts command |
| `nexusban.alts.showip` | See IP addresses in /alts |
| `nexusban.notify` | Receive staff notifications |
| `nexusban.exempt` | Cannot be punished |
| `nexusban.admin` | Admin-level staff (cannot be punished by moderators) |
| `nexusban.moderator` | Moderator-level staff |

## Installation

1. Download the latest release from [Releases](../../releases)
2. Place `NexusBan-1.0.jar` in your server's `plugins/` folder
3. Restart your server
4. Configure the plugin in `plugins/NexusBan/config.yml`

## Building from Source

Requirements:
- Java 21+
- Maven 3.6+

```bash
git clone https://github.com/yourusername/NexusBan.git
cd NexusBan
mvn clean package
```

The compiled JAR will be in `target/NexusBan-1.0.jar`

## Configuration

```yaml
# NexusBan Configuration

# Message settings
messages:
  prefix: "&8« &b&lNexus&3&lBan &8» &7"

# Ban screen appeal URL
appeal-url: "discord.gg/yourserver"

# Auto-punish settings (based on warning count)
auto-punish:
  enabled: true
  warnings-for-tempmute: 3
  warnings-for-tempban: 5
  warnings-for-ban: 10

# Duration for auto-punishments
auto-durations:
  tempmute: "1h"
  tempban: "1d"
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
