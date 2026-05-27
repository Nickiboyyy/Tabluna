package de.Lunora.tabluna.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChatCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("tabluna.chat.admin")) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            for (int i = 0; i < 100; i++) {
                Bukkit.broadcast(net.kyori.adventure.text.Component.text(" "));
            }
            Bukkit.broadcast(net.kyori.adventure.text.Component.text("§aThe chat has been cleared by §e" + sender.getName()));
            return true;
        }

        sender.sendMessage("§eChat Commands:");
        sender.sendMessage("§e/chat clear §7- Clears the chat");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("clear");
        }
        return new ArrayList<>();
    }
}
