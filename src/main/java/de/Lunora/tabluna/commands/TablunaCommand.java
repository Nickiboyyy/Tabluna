package de.Lunora.tabluna.commands;

import de.Lunora.tabluna.Tabluna;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TablunaCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("tabluna.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Tabluna.getInstance().reloadConfig();
                Tabluna.getInstance().getAnimationManager().load();
                Tabluna.getInstance().getTabManager().updateAll();
                sender.sendMessage("§aTabluna configuration and animations reloaded.");
                return true;
            }

            if (args[0].equalsIgnoreCase("info")) {
                sender.sendMessage("§8§m----------------------------------");
                sender.sendMessage("§6§lTabluna §7- §ePlugin Info");
                sender.sendMessage(" ");
                sender.sendMessage("§7Version: §f" + Tabluna.getInstance().getDescription().getVersion());
                sender.sendMessage("§7Author: §f" + String.join(", ", Tabluna.getInstance().getDescription().getAuthors()));
                sender.sendMessage("§7Features: §aHeader/Footer, Scoreboard, Animations, Nametags");
                sender.sendMessage("§8§m----------------------------------");
                return true;
            }
        }

        sender.sendMessage("§eTabluna Commands:");
        sender.sendMessage("§e/tabluna reload §7- Reloads config & animations");
        sender.sendMessage("§e/tabluna info §7- Shows plugin information");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("reload");
            completions.add("info");
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
