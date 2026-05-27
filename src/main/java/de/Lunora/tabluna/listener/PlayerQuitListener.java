package de.Lunora.tabluna.listener;

import de.Lunora.tabluna.Tabluna;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Run after player left to get correct online count
        Bukkit.getScheduler().runTaskLater(Tabluna.getInstance(), () -> {
            Tabluna.getInstance().getTabManager().updateAll();
        }, 1L);
    }
}
