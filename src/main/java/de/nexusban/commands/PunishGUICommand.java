package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.gui.PunishGUI;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishGUICommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public PunishGUICommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.PREFIX + "§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("nexusban.gui")) {
            player.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            player.sendMessage(MessageUtils.PREFIX + "§cUsage: /punish <player>");
            return true;
        }
        
        String targetName = args[0];
        
        // Prevent punishing yourself
        if (player.getName().equalsIgnoreCase(targetName)) {
            player.sendMessage(MessageUtils.PREFIX + "§cYou cannot punish yourself!");
            return true;
        }
        
        // Allow punishing ANY player name (online, offline, or never joined)
        PunishGUI.openMainMenu(player, targetName);
        
        return true;
    }
}

