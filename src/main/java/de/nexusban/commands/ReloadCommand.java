package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public ReloadCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.reload")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Reload config
            plugin.reloadConfig();
            
            long endTime = System.currentTimeMillis();
            sender.sendMessage(MessageUtils.PREFIX + "§aConfiguration reloaded successfully! §7(" + (endTime - startTime) + "ms)");
            
            // Show loaded values for confirmation
            String discordUrl = plugin.getConfig().getString("discord.invite-url", "not set");
            boolean rejoinWarning = plugin.getConfig().getBoolean("rejoin-warning.enabled", true);
            
            sender.sendMessage(MessageUtils.PREFIX + "§7Discord URL: §f" + discordUrl);
            sender.sendMessage(MessageUtils.PREFIX + "§7Rejoin Warning: §f" + (rejoinWarning ? "§aEnabled" : "§cDisabled"));
            
        } catch (Exception e) {
            sender.sendMessage(MessageUtils.PREFIX + "§cError reloading config: §f" + e.getMessage());
            plugin.getLogger().severe("Error reloading config: " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
}
