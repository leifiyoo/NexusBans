package de.meinplugin;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MeinPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("MeinPlugin wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MeinPlugin wurde deaktiviert!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nachricht")) {
            
            // Prüfen ob der Sender ein Spieler ist
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cDieser Befehl kann nur von Spielern ausgeführt werden!");
                return true;
            }
            
            Player player = (Player) sender;
            
            // Text zusammenbauen (Standard oder benutzerdefiniert)
            String text;
            if (args.length > 0) {
                text = String.join(" ", args);
            } else {
                text = "Willkommen auf dem Server!";
            }
            
            // Nachricht im Chat anzeigen
            player.sendMessage("§a§l[MeinPlugin] §r§e" + text);
            
            // Bossbar erstellen und anzeigen
            BossBar bossBar = Bukkit.createBossBar(
                "§b§l" + text,
                BarColor.BLUE,
                BarStyle.SOLID
            );
            
            bossBar.setProgress(1.0);
            bossBar.addPlayer(player);
            
            // Bossbar nach 5 Sekunden automatisch entfernen
            new BukkitRunnable() {
                @Override
                public void run() {
                    bossBar.removePlayer(player);
                }
            }.runTaskLater(this, 100L); // 100 Ticks = 5 Sekunden
            
            return true;
        }
        
        return false;
    }
}
