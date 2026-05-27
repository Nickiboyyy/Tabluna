package de.Lunora.tabluna.commands;

import de.Lunora.tabluna.Tabluna;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RulesCommand implements CommandExecutor {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    private final LegacyComponentSerializer sectionSerializer = LegacyComponentSerializer.legacySection();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern ausgeführt werden.");
            return true;
        }

        String displayMode = Tabluna.getInstance().getConfig().getString("rules.display-mode", "CHAT").toUpperCase();
        List<String> ruleLines = Tabluna.getInstance().getConfig().getStringList("rules.lines");
        String title = Tabluna.getInstance().getConfig().getString("rules.title", "&6&lServer Regeln");

        if (displayMode.equals("BOOK")) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();

            if (meta != null) {
                String bookTitle = Tabluna.getInstance().getConfig().getString("rules.book.title", "Regelbuch");
                String bookAuthor = Tabluna.getInstance().getConfig().getString("rules.book.author", "Server Team");

                meta.setTitle(sectionSerializer.serialize(parseText(bookTitle)));
                meta.setAuthor(sectionSerializer.serialize(parseText(bookAuthor)));

                StringBuilder currentPageContent = new StringBuilder();
                for (String line : ruleLines) {
                    // Check if adding the line exceeds typical page length (~250-300 chars)
                    if (currentPageContent.length() + line.length() > 250) {
                        meta.addPages(parseText(currentPageContent.toString()));
                        currentPageContent = new StringBuilder();
                    }
                    currentPageContent.append(line).append("\n");
                }

                if (currentPageContent.length() > 0) {
                    meta.addPages(parseText(currentPageContent.toString()));
                }

                book.setItemMeta(meta);
                player.openBook(book);
            }

        } else { // CHAT mode
            player.sendMessage(parseText(title));
            for (String line : ruleLines) {
                player.sendMessage(parseText(line));
            }
        }

        return true;
    }

    private Component parseText(String text) {
        if (text == null) return Component.empty();
        
        // Handle HEX/MiniMessage first
        if (text.contains("<") || text.contains("&#")) {
            String processed = text;
            if (text.contains("&#")) {
                processed = text.replaceAll("&#([A-Fa-f0-9]{6})", "<#$1>");
            }
            return miniMessage.deserialize(processed.replace("§", "&")).compact();
        }
        
        // Fallback to legacy & support
        return legacySerializer.deserialize(text.replace("§", "&"));
    }
}
