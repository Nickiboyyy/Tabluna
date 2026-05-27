package de.Lunora.tabluna.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LuckPermsHook {

    private static boolean enabled = false;

    public static void init() {
        try {
            if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
                // Wir rufen eine Methode in einer anderen Klasse auf, 
                // damit diese Klasse hier keine direkte Referenz auf LuckPerms beim Laden hat.
                enabled = LuckPermsApiHandler.check();
            }
        } catch (NoClassDefFoundError | Exception ignored) {
            enabled = false;
        }
    }

    public static String getPrefix(Player player) {
        if (!enabled) return "";
        return LuckPermsApiHandler.getPrefix(player.getUniqueId());
    }

    public static String getSuffix(Player player) {
        if (!enabled) return "";
        return LuckPermsApiHandler.getSuffix(player.getUniqueId());
    }

    public static String getGroup(Player player) {
        if (!enabled) return "default";
        return LuckPermsApiHandler.getGroup(player.getUniqueId());
    }

    // Interne Klasse, die nur geladen wird, wenn LuckPerms wirklich da ist.
    private static class LuckPermsApiHandler {
        private static LuckPerms api;

        static boolean check() {
            try {
                api = LuckPermsProvider.get();
                return api != null;
            } catch (NoClassDefFoundError | Exception e) {
                return false;
            }
        }

        static String getPrefix(UUID uuid) {
            User user = api.getUserManager().getUser(uuid);
            if (user == null) return "";
            String prefix = user.getCachedData().getMetaData().getPrefix();
            return prefix != null ? prefix : "";
        }

        static String getSuffix(UUID uuid) {
            User user = api.getUserManager().getUser(uuid);
            if (user == null) return "";
            String suffix = user.getCachedData().getMetaData().getSuffix();
            return suffix != null ? suffix : "";
        }

        static String getGroup(UUID uuid) {
            User user = api.getUserManager().getUser(uuid);
            if (user == null) return "default";
            return user.getPrimaryGroup();
        }
    }
}
