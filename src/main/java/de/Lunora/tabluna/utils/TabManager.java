package de.Lunora.tabluna.utils;

import de.Lunora.tabluna.Tabluna;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabManager {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    private final Map<Character, Character> smallCapsMap = new HashMap<>();
    private final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private final Pattern animPattern = Pattern.compile("%animation:([^%]+)%");

    public TabManager() {
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String small = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀѕᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀѕᴛᴜᴠᴡxʏᴢ";
        for (int i = 0; i < normal.length(); i++) {
            smallCapsMap.put(normal.charAt(i), small.charAt(i));
        }
    }

    public void load() {
        updateAll();
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTab(player);
            updateScoreboard(player);
        }
    }

    public void updateTab(Player player) {
        setHeaderFooter(player);
        updateObjective(player);
        updateSortingAndFormatting(player);
    }

    private void setHeaderFooter(Player player) {
        ConfigurationSection section = Tabluna.getInstance().getConfig().getConfigurationSection("header-footer");
        if (section == null || !section.getBoolean("enabled", true)) return;

        ConfigurationSection designs = section.getConfigurationSection("designs");
        if (designs == null) return;

        List<String> headerLines = null;
        List<String> footerLines = null;

        for (String key : designs.getKeys(false)) {
            if (key.equals("default")) continue;
            ConfigurationSection design = designs.getConfigurationSection(key);
            if (design != null && checkCondition(player, design.getString("display-condition"))) {
                headerLines = design.getStringList("header");
                footerLines = design.getStringList("footer");
                break;
            }
        }

        if (headerLines == null || footerLines == null) {
            ConfigurationSection def = designs.getConfigurationSection("default");
            if (def != null) {
                headerLines = def.getStringList("header");
                footerLines = def.getStringList("footer");
            }
        }

        if (headerLines != null && footerLines != null) {
            player.sendPlayerListHeaderAndFooter(
                parseText(player, String.join("\n", headerLines)),
                parseText(player, String.join("\n", footerLines))
            );
        }
    }

    private void updateObjective(Player player) {
        ConfigurationSection section = Tabluna.getInstance().getConfig().getConfigurationSection("playerlist-objective");
        Scoreboard sb = getOrCreateScoreboard(player);
        Objective obj = sb.getObjective("tab_ping");

        if (section == null || !section.getBoolean("enabled", false)) {
            if (obj != null) obj.unregister();
            return;
        }

        if (obj == null) {
            obj = sb.registerNewObjective("tab_ping", Criteria.DUMMY, parseText(player, section.getString("title", "TAB")));
            obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        String valueType = section.getString("value", "%ping%");
        for (Player online : Bukkit.getOnlinePlayers()) {
            int val = valueType.equals("%ping%") ? online.getPing() : (int) online.getHealth();
            obj.getScore(online.getName()).setScore(val);
        }
    }

    private void updateSortingAndFormatting(Player player) {
        ConfigurationSection section = Tabluna.getInstance().getConfig().getConfigurationSection("scoreboard-teams");
        if (section == null || !section.getBoolean("enabled", true)) return;

        List<String> sorting = section.getStringList("sorting-types");
        String groupsOrder = "";
        for (String s : sorting) if (s.startsWith("GROUPS:")) groupsOrder = s.substring(7);
        String[] groups = groupsOrder.split(",");

        ConfigurationSection aboveSection = Tabluna.getInstance().getConfig().getConfigurationSection("abovename");
        boolean aboveEnabled = aboveSection != null && aboveSection.getBoolean("enabled", false);
        String aboveText = aboveEnabled ? aboveSection.getString("text", "") : "";

        Scoreboard sb = getOrCreateScoreboard(player);
        
        for (Player target : Bukkit.getOnlinePlayers()) {
            String group = LuckPermsHook.getGroup(target);
            int weight = 999;
            for (int i = 0; i < groups.length; i++) {
                if (groups[i].equalsIgnoreCase(group)) {
                    weight = i;
                    break;
                }
            }

            // Teamname für Sortierung: 000Admin, 001Mod, etc.
            String teamName = String.format("%03d%s", weight, group.length() > 10 ? group.substring(0, 10) : group);
            Team team = sb.getTeam(teamName);
            if (team == null) team = sb.registerNewTeam(teamName);
            
            if (!team.hasEntry(target.getName())) {
                // Spieler aus anderen Teams entfernen, falls vorhanden (wichtig!)
                for (Team otherTeam : sb.getTeams()) {
                    if (otherTeam.hasEntry(target.getName()) && !otherTeam.getName().equals(teamName)) {
                        otherTeam.removeEntry(target.getName());
                    }
                }
                team.addEntry(target.getName());
            }
            
            // Formatierung im Tab (Prefix)
            String prefix = LuckPermsHook.getPrefix(target);
            String suffix = LuckPermsHook.getSuffix(target);
            
            // Wenn AboveName aktiviert ist, kommt es VOR den Präfix
            String finalPrefix = (aboveEnabled ? aboveText + " " : "") + prefix;
            
            target.playerListName(parseText(target, finalPrefix + target.getName() + suffix));
        }
    }

    public void updateScoreboard(Player player) {
        ConfigurationSection section = Tabluna.getInstance().getConfig().getConfigurationSection("scoreboard");
        if (section == null || !section.getBoolean("enabled", false)) return;

        ConfigurationSection sbConfig = section.getConfigurationSection("scoreboards.scoreboard");
        if (sbConfig == null) return;

        Scoreboard sb = getOrCreateScoreboard(player);
        Objective obj = sb.getObjective("sidebar");
        
        if (obj == null) {
            obj = sb.registerNewObjective("sidebar", Criteria.DUMMY, parseText(player, sbConfig.getString("title", "Server")));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            obj.displayName(parseText(player, sbConfig.getString("title", "Server")));
        }

        List<String> lines = sbConfig.getStringList("lines");
        for (int i = 0; i < lines.size(); i++) {
            int score = lines.size() - 1 - i;
            String line = lines.get(i);
            
            String teamName = "line_" + score;
            Team team = sb.getTeam(teamName);
            if (team == null) {
                team = sb.registerNewTeam(teamName);
                String entry = getEntry(score);
                team.addEntry(entry);
                obj.getScore(entry).setScore(score);
            }
            
            team.prefix(parseText(player, line));
        }
    }

    private String getEntry(int score) {
        return ChatColor.values()[score].toString() + ChatColor.RESET;
    }

    private String getPlayerGroup(Player player, String[] groups) {
        String group = LuckPermsHook.getGroup(player);
        for (String g : groups) {
            if (g.equalsIgnoreCase(group)) return g;
        }
        return "default";
    }

    private Scoreboard getOrCreateScoreboard(Player player) {
        if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        return player.getScoreboard();
    }

    private boolean checkCondition(Player player, String condition) {
        if (condition == null || condition.isEmpty()) return false;
        if (condition.startsWith("%world%=")) return player.getWorld().getName().equalsIgnoreCase(condition.split("=")[1]);
        return false;
    }

    private Component parseText(Player player, String text) {
        if (text == null) return Component.empty();
        String processed = replacePlaceholders(player, text);
        
        Matcher matcher = hexPattern.matcher(processed);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        processed = sb.toString();

        if (processed.contains("<") || processed.contains("&")) {
            processed = processed.replace("&0", "<black>")
                    .replace("&1", "<dark_blue>")
                    .replace("&2", "<dark_green>")
                    .replace("&3", "<dark_aqua>")
                    .replace("&4", "<dark_red>")
                    .replace("&5", "<dark_purple>")
                    .replace("&6", "<gold>")
                    .replace("&7", "<gray>")
                    .replace("&8", "<dark_gray>")
                    .replace("&9", "<blue>")
                    .replace("&a", "<green>")
                    .replace("&b", "<aqua>")
                    .replace("&c", "<red>")
                    .replace("&d", "<light_purple>")
                    .replace("&e", "<yellow>")
                    .replace("&f", "<white>")
                    .replace("&l", "<bold>")
                    .replace("&m", "<strikethrough>")
                    .replace("&n", "<underline>")
                    .replace("&o", "<italic>")
                    .replace("&r", "<reset>");
            
            return miniMessage.deserialize(processed).compact();
        }
        
        return legacySerializer.deserialize(processed);
    }

    private String replacePlaceholders(Player player, String text) {
        if (text == null) return "";
        
        // Handle Animations FIRST
        Matcher animMatcher = animPattern.matcher(text);
        StringBuilder animSb = new StringBuilder();
        while (animMatcher.find()) {
            String animName = animMatcher.group(1);
            animMatcher.appendReplacement(animSb, Tabluna.getInstance().getAnimationManager().getMessage(animName));
        }
        animMatcher.appendTail(animSb);
        String result = animSb.toString();

        // Then handle other placeholders
        result = result.replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%ping%", String.valueOf(player.getPing()))
                .replace("%world%", player.getWorld().getName())
                .replace("%group%", LuckPermsHook.getGroup(player))
                .replace("%prefix%", LuckPermsHook.getPrefix(player))
                .replace("%suffix%", LuckPermsHook.getSuffix(player))
                .replace("%geld%", "1.000")
                .replace("%time%", new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()))
                .replace("%date%", new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date()));
        
        if (result.contains("%smallcaps_player%")) {
            result = result.replace("%smallcaps_player%", toSmallCaps(player.getName()));
        }
        
        return result;
    }

    private String toSmallCaps(String input) {
        StringBuilder builder = new StringBuilder();
        for (char c : input.toCharArray()) {
            builder.append(smallCapsMap.getOrDefault(c, c));
        }
        return builder.toString();
    }

    public void clear() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.playerListName(null);
        }
    }
}
