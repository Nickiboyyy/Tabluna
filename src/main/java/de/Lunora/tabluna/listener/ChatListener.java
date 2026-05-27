package de.Lunora.tabluna.listener;

import de.Lunora.tabluna.Tabluna;
import de.Lunora.tabluna.utils.LuckPermsHook;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class ChatListener implements Listener {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        FileConfiguration config = Tabluna.getInstance().getConfig();
        if (!config.getBoolean("chat.enabled", true)) return;

        String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
        
        // Chat Filter
        if (config.getBoolean("chat.filter.enabled", true)) {
            List<String> forbidden = config.getStringList("chat.filter.forbidden-words");
            boolean containsForbidden = false;
            String lowerMessage = message.toLowerCase();
            
            for (String word : forbidden) {
                if (lowerMessage.contains(word.toLowerCase())) {
                    containsForbidden = true;
                    if (!config.getBoolean("chat.filter.block-entire-message", true)) {
                        message = message.replaceAll("(?i)" + word, config.getString("chat.filter.replacement", "***"));
                    }
                }
            }

            if (containsForbidden && config.getBoolean("chat.filter.block-entire-message", true)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(legacySerializer.deserialize(config.getString("chat.filter.blocked-message", "&cBlocked!").replace("&", "§")));
                return;
            }
        }

        event.viewers().clear();
        event.viewers().addAll(Bukkit.getOnlinePlayers());

        String format = config.getString("chat.format", "%luckperms_prefix%%player%%luckperms_suffix% &8» &f%message%");
        String finalMessage = message;
        
        String processed = format.replace("%player%", event.getPlayer().getName())
                .replace("%luckperms_prefix%", LuckPermsHook.getPrefix(event.getPlayer()))
                .replace("%luckperms_suffix%", LuckPermsHook.getSuffix(event.getPlayer()))
                .replace("%message%", finalMessage)
                .replace("&", "§");

        Component chatComponent = processed.contains("<") ? 
                miniMessage.deserialize(processed.replace("§", "&")) : 
                legacySerializer.deserialize(processed);

        event.renderer((source, sourceDisplayName, messageComponent, viewer) -> chatComponent);
    }
}
