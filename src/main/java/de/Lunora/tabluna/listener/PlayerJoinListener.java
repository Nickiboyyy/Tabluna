package de.Lunora.tabluna.listener;

import de.Lunora.tabluna.Tabluna;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Tabluna.getInstance().getTabManager().updateTab(event.getPlayer());
        // Update others because online count might have changed
        Tabluna.getInstance().getTabManager().updateAll();
    }
}
