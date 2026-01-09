package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CheckBanCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    // Thread-safe date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    
    public CheckBanCommand(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.checkban")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /checkban <id>");
            sender.sendMessage(MessageUtils.PREFIX + "§7The ID is shown on the ban screen (first 8 characters of UUID)");
            return true;
        }
        
        String searchId = args[0].toLowerCase();
        
        // Search in UUID-based bans
        Punishment foundBan = null;
        for (Punishment ban : plugin.getPunishmentManager().getAllBans()) {
            if (ban.getPlayerUUID() != null && ban.getPlayerUUID().toString().toLowerCase().startsWith(searchId)) {
                foundBan = ban;
                break;
            }
        }
        
        // Search in name-based bans if not found
        if (foundBan == null) {
            for (Punishment ban : plugin.getPunishmentManager().getAllNameBans()) {
                // Name bans don't have UUID, so we can't search by ID
                // But we can search by player name as fallback
                if (ban.getPlayerName().toLowerCase().startsWith(searchId)) {
                    foundBan = ban;
                    break;
                }
            }
        }
        
        if (foundBan == null) {
            sender.sendMessage(MessageUtils.PREFIX + "§cNo ban found with ID: §f" + searchId);
            sender.sendMessage(MessageUtils.PREFIX + "§7Tip: You can also use /history <player> to check punishments");
            return true;
        }
        
        // Display ban information
        String duration;
        String remaining;
        if (foundBan.isPermanent()) {
            duration = "§4Permanent";
            remaining = "§4Never";
        } else {
            duration = MessageUtils.formatDuration(foundBan.getEndTime() - foundBan.getStartTime());
            remaining = MessageUtils.formatDuration(foundBan.getRemainingTime());
        }
        
        String banDate = formatDate(foundBan.getStartTime());
        String expiryDate = foundBan.isPermanent() ? "§4Never" : formatDate(foundBan.getEndTime());
        
        sender.sendMessage("");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§b§l  🔍 BAN INFORMATION");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§7  Player: §f" + foundBan.getPlayerName());
        sender.sendMessage("§7  Type: §f" + foundBan.getType().name());
        sender.sendMessage("§7  Reason: §f" + foundBan.getReason());
        sender.sendMessage("§7  Banned by: §f" + foundBan.getPunisherName());
        sender.sendMessage("§7  Duration: §f" + duration);
        sender.sendMessage("§7  Remaining: §f" + remaining);
        sender.sendMessage("§7  Ban Date: §f" + banDate);
        sender.sendMessage("§7  Expires: §f" + expiryDate);
        if (foundBan.getPlayerUUID() != null) {
            sender.sendMessage("§7  Ban ID: §8" + foundBan.getPlayerUUID().toString().substring(0, 8));
            sender.sendMessage("§7  Full UUID: §8" + foundBan.getPlayerUUID().toString());
        } else {
            sender.sendMessage("§7  Ban ID: §8[name-based ban]");
        }
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        
        return true;
    }
    
    private String formatDate(long timestamp) {
        return DATE_FORMATTER.format(
            Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        );
    }
}
