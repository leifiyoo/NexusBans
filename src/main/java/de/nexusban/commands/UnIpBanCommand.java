package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class UnIpBanCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public UnIpBanCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.unipban")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /unipban <player|ip>");
            return true;
        }
        
        String target = args[0];
        String ip;
        
        // Check if target is an IP address or a player name
        if (isValidIp(target)) {
            ip = target;
        } else {
            // Target is a player name - get their IP
            UUID uuid = plugin.getPunishmentManager().getUUID(target);
            if (uuid != null) {
                ip = plugin.getPunishmentManager().getLastIp(uuid);
                if (ip == null) {
                    // Try to find IP ban by player name
                    ip = findIpByPlayerName(target);
                }
            } else {
                // Try to find IP ban by player name
                ip = findIpByPlayerName(target);
            }
            
            if (ip == null) {
                sender.sendMessage(MessageUtils.PREFIX + "§cNo IP ban found for: §f" + target);
                return true;
            }
        }
        
        // Check if IP is banned
        if (!plugin.getPunishmentManager().isIpBanned(ip)) {
            sender.sendMessage(MessageUtils.PREFIX + "§cThis IP is not banned!");
            return true;
        }
        
        plugin.getPunishmentManager().removeIpBan(ip);
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully removed IP ban for: §f" + ip);
        
        return true;
    }
    
    private String findIpByPlayerName(String playerName) {
        Map<String, Punishment> ipBans = plugin.getPunishmentManager().getIpBansMap();
        for (Map.Entry<String, Punishment> entry : ipBans.entrySet()) {
            if (entry.getValue().getPlayerName().equalsIgnoreCase(playerName) ||
                entry.getValue().getPlayerName().toLowerCase().contains(playerName.toLowerCase())) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private boolean isValidIp(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        
        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
