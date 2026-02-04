package de.nexusban.listeners;

import de.nexusban.NexusBan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

public class FreezeListener implements Listener {

    private final NexusBan plugin;

    public FreezeListener(NexusBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            // Cancel movement (allow head movement only)
            if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getY() != event.getTo().getY() ||
                    event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (plugin.getFreezeManager().isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (plugin.getFreezeManager().isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getFreezeManager().isFrozen(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            // Allow only specific commands (like /msg to chat with admin)
            String command = event.getMessage().toLowerCase();
            if (!command.startsWith("/msg") && !command.startsWith("/tell") &&
                !command.startsWith("/w") && !command.startsWith("/r")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getFreezeManager().isFrozen(player)) {
                // Cancel damage taken
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (plugin.getFreezeManager().isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("messages.freeze.action-blocked", "§cYou cannot do this while frozen!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player)) {
            // Notify staff that frozen player disconnected
            String message = plugin.getConfig().getString("messages.freeze.player-disconnected", "§c{player} disconnected while frozen!")
                    .replace("{player}", player.getName());

            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("nexusban.freeze")) {
                    onlinePlayer.sendMessage(message);
                }
            }
        }
    }
}
