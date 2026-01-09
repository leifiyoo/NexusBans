package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BanListCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public BanListCommand(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.banlist")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        Collection<Punishment> uuidBans = plugin.getPunishmentManager().getAllBans();
        Collection<Punishment> nameBans = plugin.getPunishmentManager().getAllNameBans();
        
        List<Punishment> allBans = new ArrayList<>();
        allBans.addAll(uuidBans);
        allBans.addAll(nameBans);
        
        sender.sendMessage("");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§c§l  🚫 ACTIVE BANS");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (allBans.isEmpty()) {
            sender.sendMessage("§7  No active bans found.");
        } else {
            int count = 0;
            for (Punishment ban : allBans) {
                if (count >= 15) {
                    sender.sendMessage("§7  ... and " + (allBans.size() - 15) + " more bans");
                    break;
                }
                
                String remaining;
                if (ban.isPermanent()) {
                    remaining = "§4Permanent";
                } else {
                    remaining = "§e" + MessageUtils.formatDuration(ban.getRemainingTime());
                }
                
                String nameTag = ban.getPlayerUUID() == null ? " §8[name]" : "";
                sender.sendMessage("§7  • §c" + ban.getPlayerName() + nameTag + " §8| §7" + remaining + " §8| §f" + ban.getReason());
                count++;
            }
        }
        
        sender.sendMessage("");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§7  Total Active Bans: §c" + allBans.size());
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        
        return true;
    }
}
