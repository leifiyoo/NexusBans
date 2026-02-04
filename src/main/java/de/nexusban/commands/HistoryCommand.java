package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public HistoryCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.history")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /history <player>");
            return true;
        }
        
        String targetName = args[0];
        UUID targetUUID = plugin.getPunishmentManager().getUUID(targetName);

        // Get history for both UUID and name (supports offline/never-joined players)
        List<Punishment> history = plugin.getHistoryManager().getAllHistory(targetUUID, targetName);
        
        sender.sendMessage("");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§c§l  📋 PUNISHMENT HISTORY");
        sender.sendMessage("§7  Player: §f" + targetName);
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (history.isEmpty()) {
            sender.sendMessage("§7  No punishment history found.");
        } else {
            int count = 0;
            for (Punishment p : history) {
                if (count >= 10) {
                    sender.sendMessage("§7  ... and " + (history.size() - 10) + " more entries");
                    break;
                }
                
                String typeColor = getTypeColor(p.getType());
                String duration = p.isPermanent() ? "Permanent" : MessageUtils.formatDuration(p.getEndTime() - p.getStartTime());
                
                sender.sendMessage("");
                sender.sendMessage("§7  #" + (count + 1) + " " + typeColor + p.getType().name());
                sender.sendMessage("§7     Reason: §f" + p.getReason());
                sender.sendMessage("§7     Staff: §f" + p.getPunisherName());
                sender.sendMessage("§7     Date: §f" + MessageUtils.formatDate(p.getStartTime()));
                if (p.getType().name().contains("BAN") || p.getType().name().contains("MUTE")) {
                    sender.sendMessage("§7     Duration: §f" + duration);
                }
                
                count++;
            }
        }
        
        sender.sendMessage("");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§7  Total Entries: §f" + history.size());
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        
        return true;
    }
    
    private String getTypeColor(Punishment.PunishmentType type) {
        switch (type) {
            case BAN:
            case TEMPBAN:
                return "§4";
            case MUTE:
            case TEMPMUTE:
                return "§6";
            case KICK:
                return "§c";
            case WARN:
                return "§e";
            default:
                return "§7";
        }
    }
}
