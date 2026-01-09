package de.meinplugin.listeners;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    
    private final MeinPlugin plugin;
    
    public PlayerChatListener(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is muted by UUID
        Punishment mute = null;
        if (plugin.getPunishmentManager().isMuted(player.getUniqueId())) {
            mute = plugin.getPunishmentManager().getMute(player.getUniqueId());
        } else if (plugin.getPunishmentManager().isNameMuted(player.getName())) {
            // Convert name mute to UUID mute
            Punishment nameMute = plugin.getPunishmentManager().getNameMute(player.getName());
            mute = new Punishment(
                player.getUniqueId(),
                player.getName(),
                nameMute.getType(),
                nameMute.getReason(),
                nameMute.getPunisherName(),
                nameMute.getStartTime(),
                nameMute.getEndTime()
            );
            plugin.getPunishmentManager().addMute(mute);
            plugin.getPunishmentManager().removeNameMute(player.getName());
        }
        
        if (mute != null) {
            event.setCancelled(true);
            
            String duration;
            if (mute.isPermanent()) {
                duration = "§4Permanent";
            } else {
                duration = MessageUtils.formatDuration(mute.getRemainingTime());
            }
            
            player.sendMessage("");
            player.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§c§l  ⚠ YOU ARE MUTED");
            player.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§7  Remaining: §f" + duration);
            player.sendMessage("§7  Reason: §f" + mute.getReason());
            player.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("");
        }
    }
}
