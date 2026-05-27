package de.Lunora.tabluna;

import de.Lunora.tabluna.commands.ChatCommand;
import de.Lunora.tabluna.commands.RulesCommand;
import de.Lunora.tabluna.commands.TablunaCommand;
import de.Lunora.tabluna.listener.ChatListener;
import de.Lunora.tabluna.listener.PlayerJoinListener;
import de.Lunora.tabluna.listener.PlayerQuitListener;
import de.Lunora.tabluna.utils.AnimationManager;
import de.Lunora.tabluna.utils.LuckPermsHook;
import de.Lunora.tabluna.utils.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Tabluna extends JavaPlugin {

    private static Tabluna instance;
    private TabManager tabManager;
    private AnimationManager animationManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        LuckPermsHook.init();

        animationManager = new AnimationManager();
        animationManager.load();

        tabManager = new TabManager();
        tabManager.load();

        TablunaCommand tablunaCmd = new TablunaCommand();
        getCommand("tabluna").setExecutor(tablunaCmd);
        getCommand("tabluna").setTabCompleter(tablunaCmd);

        ChatCommand chatCmd = new ChatCommand();
        getCommand("chat").setExecutor(chatCmd);
        getCommand("chat").setTabCompleter(chatCmd);

        getCommand("rules").setExecutor(new RulesCommand());

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            tabManager.updateAll();
        }, 0L, 2L);

        getLogger().info("Tabluna enabled with ChatFilter and Rules!");
    }

    @Override
    public void onDisable() {
        if (tabManager != null) {
            tabManager.clear();
        }
    }

    public static Tabluna getInstance() {
        return instance;
    }

    public TabManager getTabManager() {
        return tabManager;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }
}
