package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PunishTabCompleter implements TabCompleter {
    
    private final MeinPlugin plugin;
    
    public PunishTabCompleter(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        String cmd = command.getName().toLowerCase();
        
        switch (cmd) {
            case "ban":
                if (args.length == 1) {
                    completions.addAll(getPlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                } else if (args.length == 2) {
                    completions.add("<reason>");
                }
                break;
                
            case "tempban":
                if (args.length == 1) {
                    completions.addAll(getPlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                } else if (args.length == 2) {
                    completions.add("<time>");
                    completions.addAll(Arrays.asList("1h", "1d", "7d", "30d"));
                } else if (args.length == 3) {
                    completions.add("<reason>");
                }
                break;
                
            case "unban":
                if (args.length == 1) {
                    completions.addAll(plugin.getPunishmentManager().getAllBans().stream()
                        .map(p -> p.getPlayerName())
                        .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList()));
                    if (completions.isEmpty()) completions.add("<player>");
                }
                break;
                
            case "kick":
                if (args.length == 1) {
                    completions.addAll(getOnlinePlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                } else if (args.length == 2) {
                    completions.add("<reason>");
                }
                break;
                
            case "mute":
                if (args.length == 1) {
                    completions.addAll(getPlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                } else if (args.length == 2) {
                    completions.add("<reason>");
                }
                break;
                
            case "tempmute":
                if (args.length == 1) {
                    completions.addAll(getPlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                } else if (args.length == 2) {
                    completions.add("<time>");
                    completions.addAll(Arrays.asList("10m", "1h", "1d", "7d"));
                } else if (args.length == 3) {
                    completions.add("<reason>");
                }
                break;
                
            case "unmute":
                if (args.length == 1) {
                    completions.addAll(plugin.getPunishmentManager().getAllMutes().stream()
                        .map(p -> p.getPlayerName())
                        .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList()));
                    if (completions.isEmpty()) completions.add("<player>");
                }
                break;
                
            case "warn":
                if (args.length == 1) {
                    completions.addAll(getOnlinePlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                } else if (args.length == 2) {
                    completions.add("<reason>");
                }
                break;
                
            case "history":
                if (args.length == 1) {
                    completions.addAll(getPlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                }
                break;
                
            case "punish":
                if (args.length == 1) {
                    completions.addAll(getPlayerNames(args[0]));
                    if (completions.isEmpty()) completions.add("<player>");
                }
                break;
        }
        
        return completions;
    }
    
    private List<String> getPlayerNames(String prefix) {
        List<String> names = new ArrayList<>();
        
        // Add online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                names.add(player.getName());
            }
        }
        
        // Add offline players who have played before
        for (org.bukkit.OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
            if (offline.getName() != null && 
                offline.getName().toLowerCase().startsWith(prefix.toLowerCase()) &&
                !names.contains(offline.getName())) {
                names.add(offline.getName());
            }
        }
        
        return names;
    }
    
    private List<String> getOnlinePlayerNames(String prefix) {
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
            .collect(Collectors.toList());
    }
}

