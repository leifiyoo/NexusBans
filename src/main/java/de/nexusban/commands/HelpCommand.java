package de.nexusban.commands;

import de.nexusban.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("");
        sender.sendMessage("§8§m─────────────────────────────────────");
        sender.sendMessage("§b§lNexus§3§lBan §8- §7Command Help");
        sender.sendMessage("§8§m─────────────────────────────────────");
        sender.sendMessage("");
        
        // Ban Commands
        sender.sendMessage("§4§lBan Commands:");
        sender.sendMessage("  §c/ban <player> [reason] §8- §7Permanent ban");
        sender.sendMessage("  §c/tempban <player> <time> [reason] §8- §7Temporary ban");
        sender.sendMessage("  §c/unban <player> §8- §7Remove a ban");
        sender.sendMessage("  §c/ipban <player|ip> [reason] §8- §7IP ban");
        sender.sendMessage("  §c/unipban <player|ip> §8- §7Remove IP ban");
        sender.sendMessage("");
        
        // Mute Commands
        sender.sendMessage("§6§lMute Commands:");
        sender.sendMessage("  §e/mute <player> [reason] §8- §7Permanent mute");
        sender.sendMessage("  §e/tempmute <player> <time> [reason] §8- §7Temporary mute");
        sender.sendMessage("  §e/unmute <player> §8- §7Remove a mute");
        sender.sendMessage("");
        
        // Other Punishment Commands
        sender.sendMessage("§a§lOther Commands:");
        sender.sendMessage("  §f/kick <player> [reason] §8- §7Kick player");
        sender.sendMessage("  §f/warn <player> [reason] §8- §7Warn player");
        sender.sendMessage("  §f/punish <player> §8- §7Open punishment GUI");
        sender.sendMessage("");
        
        // Info Commands
        sender.sendMessage("§b§lInfo Commands:");
        sender.sendMessage("  §3/history <player> §8- §7View punishment history");
        sender.sendMessage("  §3/banlist §8- §7List all active bans");
        sender.sendMessage("  §3/ipbanlist §8- §7List all IP bans");
        sender.sendMessage("  §3/checkban <id> §8- §7Look up ban by ID");
        sender.sendMessage("  §3/alts <player> §8- §7Check for alt accounts");
        sender.sendMessage("");
        
        // Admin Commands
        sender.sendMessage("§d§lAdmin Commands:");
        sender.sendMessage("  §5/nbreload §8- §7Reload configuration");
        sender.sendMessage("");
        
        // Time format info
        sender.sendMessage("§7§lTime Format: §f1h, 1d, 7d, 2w, 1M, 1y");
        sender.sendMessage("§8§m─────────────────────────────────────");
        
        return true;
    }
}
