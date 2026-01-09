package de.nexusban;

import de.nexusban.commands.*;
import de.nexusban.listeners.*;
import de.nexusban.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class NexusBan extends JavaPlugin {

    private static NexusBan instance;
    private PunishmentManager punishmentManager;
    private HistoryManager historyManager;
    
    public static final String PLUGIN_NAME = "NexusBan";

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        punishmentManager = new PunishmentManager(this);
        historyManager = new HistoryManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("");
        getLogger().info("╔═══════════════════════════════════════════╗");
        getLogger().info("║  NexusBan v" + getDescription().getVersion() + " enabled!                 ║");
        getLogger().info("╚═══════════════════════════════════════════╝");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        if (punishmentManager != null) {
            punishmentManager.saveAll();
        }
        if (historyManager != null) {
            historyManager.saveAll();
        }
        getLogger().info(PLUGIN_NAME + " has been disabled!");
    }

    private void registerCommands() {
        PunishTabCompleter tabCompleter = new PunishTabCompleter(this);
        
        // Ban commands
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("ban").setTabCompleter(tabCompleter);
        
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("tempban").setTabCompleter(tabCompleter);
        
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("unban").setTabCompleter(tabCompleter);
        
        // Kick command
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("kick").setTabCompleter(tabCompleter);
        
        // Mute commands
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("mute").setTabCompleter(tabCompleter);
        
        getCommand("tempmute").setExecutor(new TempMuteCommand(this));
        getCommand("tempmute").setTabCompleter(tabCompleter);
        
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("unmute").setTabCompleter(tabCompleter);
        
        // Warn command
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("warn").setTabCompleter(tabCompleter);
        
        // Utility commands
        getCommand("history").setExecutor(new HistoryCommand(this));
        getCommand("history").setTabCompleter(tabCompleter);
        
        getCommand("punish").setExecutor(new PunishGUICommand(this));
        getCommand("punish").setTabCompleter(tabCompleter);
        
        getCommand("banlist").setExecutor(new BanListCommand(this));
        getCommand("banlist").setTabCompleter(tabCompleter);
        
        getCommand("checkban").setExecutor(new CheckBanCommand(this));
        
        // IP ban commands
        getCommand("ipban").setExecutor(new IpBanCommand(this));
        getCommand("ipban").setTabCompleter(tabCompleter);
        
        getCommand("unipban").setExecutor(new UnIpBanCommand(this));
        getCommand("unipban").setTabCompleter(tabCompleter);
        
        getCommand("ipbanlist").setExecutor(new IpBanListCommand(this));
        
        getCommand("alts").setExecutor(new AltsCommand(this));
        getCommand("alts").setTabCompleter(tabCompleter);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    public static NexusBan getInstance() {
        return instance;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}

