package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class IpBanListCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public IpBanListCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.ipbanlist")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        Collection<Punishment> ipBans = plugin.getPunishmentManager().getAllIpBans();
        
        sender.sendMessage("");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§c§l  ⛔ ACTIVE IP BANS");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (ipBans.isEmpty()) {
            sender.sendMessage("§7  No active IP bans.");
        } else {
            sender.sendMessage("§7  Total: §f" + ipBans.size() + " IP ban(s)");
            sender.sendMessage("");
            
            int count = 0;
            for (Punishment ban : ipBans) {
                if (count >= 20) {
                    sender.sendMessage("§7  ... and " + (ipBans.size() - 20) + " more");
                    break;
                }
                
                String duration = ban.isPermanent() ? "§4Permanent" : MessageUtils.formatDuration(ban.getRemainingTime());
                sender.sendMessage("§8  - §f" + ban.getPlayerName());
                sender.sendMessage("§8      Reason: §7" + ban.getReason());
                sender.sendMessage("§8      By: §e" + ban.getPunisherName() + " §8| §7Duration: " + duration);
                count++;
            }
        }
        
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        
        return true;
    }
}
