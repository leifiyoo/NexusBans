package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.data.Punishment.PunishmentType;
import de.nexusban.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IpBanCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public IpBanCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.ipban")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /ipban <player|ip> [reason]");
            return true;
        }
        
        String target = args[0];
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason specified";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        String ip;
        String playerName;
        
        // Check if target is an IP address or a player name
        if (isValidIp(target)) {
            ip = target;
            playerName = "IP: " + ip;
        } else {
            // Target is a player name - get their IP
            Player onlinePlayer = plugin.getServer().getPlayerExact(target);
            if (onlinePlayer != null) {
                ip = onlinePlayer.getAddress().getAddress().getHostAddress();
                playerName = onlinePlayer.getName();
                
                // Staff protection check
                if (!plugin.getPunishmentManager().canPunish(sender, onlinePlayer.getUniqueId())) {
                    sender.sendMessage(MessageUtils.PREFIX + "§cYou cannot punish this player!");
                    return true;
                }
            } else {
                // Try to get last known IP from player data
                UUID uuid = plugin.getPunishmentManager().getUUID(target);
                if (uuid != null) {
                    ip = plugin.getPunishmentManager().getLastIp(uuid);
                    playerName = target;
                    
                    if (ip == null) {
                        sender.sendMessage(MessageUtils.PREFIX + "§cNo IP found for player: §f" + target);
                        return true;
                    }
                } else {
                    sender.sendMessage(MessageUtils.PREFIX + "§cPlayer not found and no valid IP provided!");
                    return true;
                }
            }
        }
        
        // Check if IP is already banned
        if (plugin.getPunishmentManager().isIpBanned(ip)) {
            sender.sendMessage(MessageUtils.PREFIX + "§cThis IP is already banned!");
            return true;
        }
        
        // Check for dangerous IPs that should not be banned
        if (isLocalOrDangerousIp(ip)) {
            sender.sendMessage(MessageUtils.PREFIX + "§c§lWARNING: §cCannot ban this IP!");
            sender.sendMessage(MessageUtils.PREFIX + "§7This is a localhost/local network IP (§f" + ip + "§7)");
            sender.sendMessage(MessageUtils.PREFIX + "§7Banning it would affect ALL local connections!");
            return true;
        }
        
        // Create punishment
        Punishment punishment = new Punishment(
            null,
            playerName,
            PunishmentType.IPBAN,
            reason,
            punisherName,
            System.currentTimeMillis(),
            -1 // Permanent
        );
        
        plugin.getPunishmentManager().addIpBan(ip, punishment);
        
        // Kick all players with this IP
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.getAddress().getAddress().getHostAddress().equals(ip)) {
                online.kickPlayer(String.join("\n",
                    "",
                    "§c§l✦ YOUR IP HAS BEEN BANNED ✦",
                    "",
                    "§7Reason: §f" + reason,
                    "§7Duration: §fPermanent",
                    "§7Banned by: §f" + punisherName,
                    "",
                    "§7Appeal at: §e§ndiscord.gg/yourserver",
                    ""
                ));
            }
        }
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("IP-BAN", playerName + " (" + ip + ")", punisherName, reason, "Permanent");
        plugin.getServer().getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("nexusban.notify"))
            .forEach(p -> p.sendMessage(broadcast));
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully IP-banned §f" + ip + " §a(§f" + playerName + "§a)");
        
        return true;
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
    
    /**
     * Checks if an IP is a localhost or local network IP that should not be banned.
     * These IPs are shared by multiple users and banning them would affect everyone.
     */
    private boolean isLocalOrDangerousIp(String ip) {
        // Localhost (127.x.x.x) - EVERYONE on same machine has this
        if (ip.startsWith("127.")) {
            return true;
        }
        
        // 0.0.0.0 - Invalid/wildcard
        if (ip.equals("0.0.0.0")) {
            return true;
        }
        
        // Private network ranges - often shared in local networks
        // 10.x.x.x
        if (ip.startsWith("10.")) {
            return true;
        }
        
        // 192.168.x.x
        if (ip.startsWith("192.168.")) {
            return true;
        }
        
        // 172.16.x.x - 172.31.x.x (private range)
        if (ip.startsWith("172.")) {
            try {
                String[] parts = ip.split("\\.");
                int secondOctet = Integer.parseInt(parts[1]);
                if (secondOctet >= 16 && secondOctet <= 31) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        
        return false;
    }
}
