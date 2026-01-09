package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public HistoryCommand(MeinPlugin plugin) {
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
        
        if (targetUUID == null) {
            sender.sendMessage(MessageUtils.PREFIX + "§cPlayer §f" + targetName + " §cwas not found!");
            return true;
        }
        
        List<Punishment> history = plugin.getHistoryManager().getHistory(targetUUID);
        
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
