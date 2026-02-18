<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.4-green?style=for-the-badge&logo=minecraft" alt="Minecraft">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/Spigot-API-yellow?style=for-the-badge" alt="Spigot">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License">
</p>

<h1 align="center">🛡️ NexusBan</h1>

<p align="center">
  <a href="https://modrinth.com/plugin/bansnexus">
<img alt="modrinth" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_46h.png">
  </a>
</p>

<p align="center">
  <b>Advanced punishment & moderation system for Minecraft servers</b><br>
  <i>Complete staff tools with freeze system, warnings, bans, mutes & more</i>
</p>

---

## ✨ Features

### 🔨 Punishment System
- **Bans** - Permanent & temporary bans with auto-expiry
- **IP Bans** - Block entire IP addresses + alt detection
- **Mutes** - Permanent & temporary chat restrictions
- **Kicks** - Instantly remove players
- **Warnings** - Track violations (works offline too!)

### 🧊 Freeze System
- **Complete restriction** - Frozen players cannot move, interact, or use commands
- **Staff-only chat** - Frozen players can only talk to admins
- **No message visibility** - Frozen players don't see regular chat
- **Disconnect alerts** - Staff notified if frozen player logs out

### 💬 Staff Tools
- **Staff Chat** (`/staffchat`) - Private team communication
- **Interactive GUI** - Point-and-click punishment interface
- **History Tracking** - Complete punishment records
- **Alt Detection** - Find alternate accounts by IP
- **Staff Protection** - Hierarchical permission system

### ⚡ Performance
- **Async I/O** - Zero server lag
- **Smart caching** - Optimized data storage
- **Thread-safe** - ConcurrentHashMap usage

---

## 📦 Installation

1. **Download** the latest release from [Releases](../../releases)
2. **Place** JAR file into your `plugins/` folder
3. **Restart** your server
4. **Configure** messages and settings in `plugins/NexusBan/config.yml`

**Requirements:**
- Minecraft 1.21.4 (compatible with 1.13+)
- Java 21
- Spigot/Paper server

---

## 🎮 Commands

### Punishment Commands
```
/ban <player> [reason]              - Permanently ban a player
/tempban <player> <time> [reason]   - Temporarily ban a player
/unban <player>                     - Remove a ban
/ipban <player|ip> [reason]         - Ban an IP address
/unipban <player|ip>                - Remove IP ban

/mute <player> [reason]             - Permanently mute a player
/tempmute <player> <time> [reason]  - Temporarily mute a player
/unmute <player>                    - Remove a mute

/kick <player> [reason]             - Kick a player
/warn <player> [reason]             - Warn a player (works offline!)
```

### Staff Tools
```
/freeze <player>                    - Freeze a player (complete restriction)
/unfreeze <player>                  - Unfreeze a player
/staffchat <message>                - Send message to staff (alias: /sc)
/punish <player>                    - Open punishment GUI (alias: /p)
```

### Information Commands
```
/history <player>                   - View punishment history
/banlist                            - List all active bans
/ipbanlist                          - List all IP bans
/alts <player>                      - Check for alt accounts
/checkban <id>                      - Look up specific ban details
/nbreload                           - Reload configuration
/nbhelp                             - Show command help
```

### ⏱️ Time Format
```
s = Seconds    m = Minutes    h = Hours
d = Days       w = Weeks      M = Months    y = Years

Examples:
  30m          = 30 minutes
  2h           = 2 hours
  7d           = 7 days
  1d12h30m     = 1 day, 12 hours, 30 minutes
```

---

## 🔐 Permissions

| Permission | Description |
|------------|-------------|
| `nexusban.*` | All permissions |
| `nexusban.ban` | Ban/tempban/unban commands |
| `nexusban.ipban` | IP ban commands |
| `nexusban.mute` | Mute/tempmute/unmute commands |
| `nexusban.kick` | Kick command |
| `nexusban.warn` | Warn command |
| `nexusban.freeze` | Freeze/unfreeze commands |
| `nexusban.freeze.bypass` | Cannot be frozen |
| `nexusban.staffchat` | Use staff chat |
| `nexusban.gui` | Use /punish GUI |
| `nexusban.history` | View punishment history |
| `nexusban.banlist` | View ban lists |
| `nexusban.alts` | Check for alts |
| `nexusban.alts.showip` | See IP addresses in /alts |
| `nexusban.notify` | Receive staff notifications |
| `nexusban.exempt` | Cannot be punished |
| `nexusban.admin` | Admin role (can punish moderators) |
| `nexusban.moderator` | Moderator role |
| `nexusban.bypass.protection` | Bypass all staff protection |
| `nexusban.reload` | Reload configuration |

---

## ⚙️ Configuration

```yaml
# Message settings
messages:
  prefix: "&8[&c&lNEXUS&4&lBAN&8] &7"

  # Staff Chat format
  staffchat-format: "&8[&c&lSTAFF&8] &e{sender} &8» &f{message}"

  # Freeze system messages
  freeze:
    frozen: "&c&l⚠ YOU HAVE BEEN FROZEN ⚠\n&7You cannot move or interact..."
    chat-format: "&8[&c&lFROZEN&8] &e{player} &8» &f{message}"

# Discord invite (shown on ban screens)
discord:
  invite-url: "discord.gg/yourserver"
  server-name: "Our Discord"

# Rejoin warnings
rejoin-warning:
  enabled: true
  show-for-hours: 168  # Show warning for 7 days after punishment

# Auto-punish system
auto-punish:
  enabled: true
  warnings-for-tempmute: 3
  warnings-for-tempban: 5
  warnings-for-ban: 10
```

---

## 🔧 Building from Source

```bash
git clone https://github.com/leifiyoo/NexusBans.git
cd NexusBans
mvn clean package
```

The compiled JAR will be in `target/NexusBan-1.1.1.jar`

---

## 📋 Version History

**v1.1.1** - Staff Chat & Freeze System
- Added staff chat system (`/staffchat`, `/sc`)
- Added complete freeze system (`/freeze`, `/unfreeze`)
- Frozen players fully restricted (movement, actions, chat)
- Offline warning support
- Improved prefix and color codes
- Fixed GUI spam-click issues
- Added tab completion for all commands

**v1.0.0** - Initial Release
- Full punishment system (ban, mute, kick, warn)
- IP bans and alt detection
- Interactive GUI
- Punishment history tracking

---

## 🐛 Bug Reports & Feature Requests

Found a bug or have a feature idea?
- Open an issue on [GitHub Issues](../../issues)
- Include Minecraft version, plugin version, and error logs

---

## 📝 License

Licensed under the **MIT License** - see [LICENSE](LICENSE) file for details.

---

<p align="center">
  Made with ❤️ for the Minecraft community
</p>
