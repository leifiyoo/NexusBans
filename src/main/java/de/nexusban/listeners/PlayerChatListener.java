package de.nexusban.listeners;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    
    private final NexusBan plugin;
    
    public PlayerChatListener(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Check if player is frozen
        if (plugin.getFreezeManager().isFrozen(player)) {
            event.setCancelled(true);

            // Send message only to admins with freeze permission
            String message = plugin.getConfig().getString("messages.freeze.chat-format", "§c[FROZEN] §e{player}§7: §f{message}")
                    .replace("{player}", player.getName())
                    .replace("{message}", event.getMessage());

            for (Player recipient : event.getRecipients()) {
                if (recipient.hasPermission("nexusban.freeze")) {
                    recipient.sendMessage(message);
                }
            }

            // Send confirmation to frozen player
            player.sendMessage(plugin.getConfig().getString("messages.freeze.chat-sent", "§7[To Staff] §f{message}")
                    .replace("{message}", event.getMessage()));

            return;
        }

        // Remove frozen players from recipients
        event.getRecipients().removeIf(recipient -> plugin.getFreezeManager().isFrozen(recipient));

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
