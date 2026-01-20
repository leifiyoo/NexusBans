<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.13--1.21+-green?style=for-the-badge&logo=minecraft" alt="Minecraft">
  <img src="https://img.shields.io/badge/Java-8+-orange?style=for-the-badge&logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/Spigot-API-yellow?style=for-the-badge" alt="Spigot">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License">
</p>

<h1 align="center">🛡️ NexusBan</h1>

<p align="center">
  <b>A powerful, modern punishment system for Minecraft Spigot servers</b><br>
  <i>Keep your server safe with advanced moderation tools</i>
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-installation">Installation</a> •
  <a href="#-commands">Commands</a> •
  <a href="#-permissions">Permissions</a> •
  <a href="#%EF%B8%8F-configuration">Configuration</a>
</p>

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 🔨 Punishment System
- **Permanent Bans** - Ban troublemakers forever
- **Temporary Bans** - Time-based bans with auto-expiry
- **IP Bans** - Block by IP address
- **Mutes** - Permanent & temporary mutes
- **Kicks** - Remove players instantly
- **Warnings** - Track player behavior

</td>
<td width="50%">

### 🔍 Advanced Features
- **Alt Detection** - Find alt accounts by IP
- **Punishment GUI** - Easy point-and-click interface
- **Ban History** - Complete punishment records
- **Staff Protection** - Prevent staff from punishing each other
- **Async I/O** - Zero server lag
- **Auto-Punish** - Automatic escalation based on warnings

</td>
</tr>
</table>

---

## 📦 Installation

1. **Download** the latest release from [Releases](../../releases)
2. **Drop** `NexusBan-1.0.jar` into your server's `plugins/` folder
3. **Restart** your server
4. **Configure** in `plugins/NexusBan/config.yml`

---

## 🎮 Commands

### Ban Commands
| Command | Description | Example |
|---------|-------------|---------|
| `/ban <player> [reason]` | Permanent ban | `/ban Griefer123 Griefing spawn` |
| `/tempban <player> <time> [reason]` | Temporary ban | `/tempban Hacker 7d Using xray` |
| `/unban <player>` | Remove a ban | `/unban Griefer123` |
| `/ipban <player\|ip> [reason]` | IP ban | `/ipban Evader Alt account` |
| `/unipban <player\|ip>` | Remove IP ban | `/unipban 192.168.1.1` |

### Mute Commands
| Command | Description | Example |
|---------|-------------|---------|
| `/mute <player> [reason]` | Permanent mute | `/mute Spammer Spam in chat` |
| `/tempmute <player> <time> [reason]` | Temporary mute | `/tempmute Toxic 1h Toxic behavior` |
| `/unmute <player>` | Remove mute | `/unmute Spammer` |

### Other Commands
| Command | Description |
|---------|-------------|
| `/kick <player> [reason]` | Kick from server |
| `/warn <player> [reason]` | Issue a warning |
| `/punish <player>` | Open punishment GUI |
| `/history <player>` | View punishment history |
| `/banlist` | List all active bans |
| `/ipbanlist` | List all IP bans |
| `/alts <player>` | Check for alt accounts |
| `/checkban <id>` | Look up ban details |
| `/nbreload` | Reload configuration |

### ⏱️ Time Format

```
s = Seconds    m = Minutes    h = Hours
d = Days       w = Weeks      M = Months    y = Years

Examples: 30m, 2h, 7d, 2w, 1M, 1y
Combined: 1d12h30m = 1 day, 12 hours, 30 minutes
```

---

## 🔐 Permissions

<details>
<summary><b>Click to expand permission list</b></summary>

| Permission | Description | Default |
|------------|-------------|---------|
| `nexusban.*` | All permissions | OP |
| `nexusban.ban` | Use /ban | OP |
| `nexusban.tempban` | Use /tempban | OP |
| `nexusban.unban` | Use /unban | OP |
| `nexusban.ipban` | Use /ipban | OP |
| `nexusban.unipban` | Use /unipban | OP |
| `nexusban.ipbanlist` | Use /ipbanlist | OP |
| `nexusban.kick` | Use /kick | OP |
| `nexusban.mute` | Use /mute | OP |
| `nexusban.tempmute` | Use /tempmute | OP |
| `nexusban.unmute` | Use /unmute | OP |
| `nexusban.warn` | Use /warn | OP |
| `nexusban.history` | Use /history | OP |
| `nexusban.gui` | Use /punish GUI | OP |
| `nexusban.banlist` | Use /banlist | OP |
| `nexusban.checkban` | Use /checkban | OP |
| `nexusban.alts` | Use /alts | OP |
| `nexusban.alts.showip` | See IPs in /alts | OP |
| `nexusban.reload` | Use /nbreload | OP |
| `nexusban.notify` | Receive staff alerts | OP |
| `nexusban.exempt` | Cannot be punished | false |
| `nexusban.admin` | Admin-level staff | false |
| `nexusban.moderator` | Moderator-level | false |

</details>

---

## ⚙️ Configuration

```yaml
# NexusBan Configuration

# Message settings
messages:
  prefix: "&8« &b&lNexus&3&lBan &8» &7"

# Ban screen appeal URL (shown to banned players)
appeal-url: "discord.gg/yourserver"

# Auto-punish (escalate warnings automatically)
auto-punish:
  enabled: true
  warnings-for-tempmute: 3   # 3 warnings = temp mute
  warnings-for-tempban: 5    # 5 warnings = temp ban
  warnings-for-ban: 10       # 10 warnings = permanent ban

# Auto-punishment durations
auto-durations:
  tempmute: "1h"
  tempban: "1d"
```

---

## 🔧 Building from Source

```bash
# Clone the repository
git clone https://github.com/leifiyoo/NexusBans.git

# Navigate to directory
cd NexusBans

# Build with Maven
mvn clean package
```

The compiled JAR will be in `target/NexusBan-1.0.jar`

---

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

Contributions are welcome! Feel free to:
- 🐛 Report bugs
- 💡 Suggest features
- 🔧 Submit pull requests

---

<p align="center">
  Made with ❤️ for the Minecraft community
</p>
