package de.nexusban.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    
    // Cached regex pattern for duration parsing (case-insensitive for s,m,h,d,w but NOT M)
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhdwMy])");
    
    // Thread-safe date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    
    // Prefix for all messages
    public static final String PREFIX = "§8« §b§lNexus§3§lBan §8» §7";
    public static final String LINE = "§8§m                                                  ";
    
    // Fancy ban screen
    public static String[] getBanScreen(String reason, String punisher, String duration, String id) {
        return new String[] {
            "",
            "§c§l✦ YOU HAVE BEEN BANNED ✦",
            "",
            "§7Reason: §f" + reason,
            "§7Duration: §f" + duration,
            "§7Banned by: §f" + punisher,
            "",
            "§7Appeal at: §e§nSee /appeal",
            "§8ID: " + id,
            ""
        };
    }
    
    // Fancy kick screen
    public static String[] getKickScreen(String reason, String punisher) {
        return new String[] {
            "",
            "§6§l✦ YOU HAVE BEEN KICKED ✦",
            "",
            "§7Reason: §f" + reason,
            "§7Kicked by: §f" + punisher,
            "",
            "§aYou may rejoin the server.",
            ""
        };
    }
    
    // Fancy mute message
    public static String getMuteMessage(String reason, String duration) {
        return PREFIX + "§cYou are muted! §7(" + duration + ")\n" +
               PREFIX + "§7Reason: §f" + reason;
    }
    
    // Staff broadcast message
    public static String getStaffBroadcast(String action, String target, String staff, String reason, String duration) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("§c§l  ⚠ PUNISHMENT ISSUED\n");
        sb.append("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("§7  Action: §f").append(action).append("\n");
        sb.append("§7  Target: §c").append(target).append("\n");
        sb.append("§7  Staff: §e").append(staff).append("\n");
        sb.append("§7  Reason: §f").append(reason).append("\n");
        if (duration != null) {
            sb.append("§7  Duration: §f").append(duration).append("\n");
        }
        sb.append("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        return sb.toString();
    }
    
    // Convert milliseconds to readable format
    public static String formatDuration(long millis) {
        if (millis == -1) return "§4Permanent";
        
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");
        
        return sb.toString().trim();
    }
    
    // Parse duration string like "1d", "2h", "30m", "1d12h30m", "1M" (month)
    public static long parseDuration(String input) {
        if (input == null || input.isEmpty()) return -1;
        
        long totalMillis = 0;
        // Don't lowercase - we need to distinguish 'm' (minutes) from 'M' (months)
        Matcher matcher = DURATION_PATTERN.matcher(input);
        
        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);
            
            switch (unit) {
                case "s": totalMillis += TimeUnit.SECONDS.toMillis(value); break;
                case "m": totalMillis += TimeUnit.MINUTES.toMillis(value); break;
                case "h": totalMillis += TimeUnit.HOURS.toMillis(value); break;
                case "d": totalMillis += TimeUnit.DAYS.toMillis(value); break;
                case "w": totalMillis += TimeUnit.DAYS.toMillis(value * 7); break;
                case "M": totalMillis += TimeUnit.DAYS.toMillis(value * 30); break;
                case "y": totalMillis += TimeUnit.DAYS.toMillis(value * 365); break;
            }
        }
        
        return totalMillis > 0 ? totalMillis : -1;
    }
    
    // Format date (thread-safe)
    public static String formatDate(long timestamp) {
        return DATE_FORMATTER.format(
            java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime()
        );
    }
    
    // Colorize message
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    // Send formatted message
    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }
}
