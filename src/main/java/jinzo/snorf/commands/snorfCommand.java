package jinzo.snorf.commands;

import jinzo.snorf.utils.CommandHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class snorfCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            CommandHelper.sendError(sender, "Usage: /s <subcommand>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "wand" -> {
                return wandSnorf.onCommand(sender, command, label, args);
            }
            case "set" -> {
                return setSnorf.onCommand(sender, command, label, args);
            }
            case "delete" -> {
                return deleteSnorf.onCommand(sender, command, label, args);
            }
            case "pos" -> {
                return posSnorf.onCommand(sender, command, label, args);
            }
            case "copy" -> {
                return copySnorf.onCommand(sender, command, label, args);
            }
            case "cut" -> {
                return cutSnorf.onCommand(sender, command, label, args);
            }
            case "paste" -> {
                return pasteSnorf.onCommand(sender, command, label, args);
            }
            default -> {
                CommandHelper.sendError(sender, "Invalid subcommand: " + args[0]);
                return false;
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            return List.of("wand", "set", "delete", "pos", "copy", "cut", "paste");
        }

        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return List.of(Material.values()).stream()
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .filter(name -> name.startsWith(args[1].toLowerCase()))
                    .limit(20)
                    .toList();
        }

        if (args[0].equalsIgnoreCase("pos")) {
            if (args.length == 2) {
                return List.of("1", "2");
            }
            if (args.length >= 3 && args.length <= 5) {
                if (sender instanceof Player) {
                    Location loc = player.getLocation();
                    if (args.length == 3) {
                        return List.of(String.valueOf(loc.getBlockX()));
                    } else if (args.length == 4) {
                        return List.of(String.valueOf(loc.getBlockY()));
                    } else if (args.length == 5) {
                        return List.of(String.valueOf(loc.getBlockZ()));
                    }
                }
            }
        }

        return List.of();
    }
}
