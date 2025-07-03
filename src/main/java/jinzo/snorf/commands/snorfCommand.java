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

    private static final List<String> SUBCOMMANDS = List.of(
            "wand", "set", "delete", "pos", "copy", "cut", "paste",
            "select", "fill", "replace", "count", "center", "undo", "redo", "restore"
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            CommandHelper.sendError(sender, "Usage: /s <subcommand>");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        return switch (subcommand) {
            case "wand" -> wandSnorf.onCommand(sender, command, label, args);
            case "set" -> setSnorf.onCommand(sender, command, label, args);
            case "delete" -> deleteSnorf.onCommand(sender, command, label, args);
            case "pos" -> posSnorf.onCommand(sender, command, label, args);
            case "copy" -> copySnorf.onCommand(sender, command, label, args);
            case "cut" -> cutSnorf.onCommand(sender, command, label, args);
            case "paste" -> pasteSnorf.onCommand(sender, command, label, args);
            case "select" -> selectSnorf.onCommand(sender, command, label, args);
            case "fill" -> fillSnorf.onCommand(sender, command, label, args);
            case "replace" -> replaceSnorf.onCommand(sender, command, label, args);
            case "count" -> countSnorf.onCommand(sender, command, label, args);
            case "center" -> centerSnorf.onCommand(sender, command, label, args);
            case "undo" -> undoSnorf.onCommand(sender, command, label, args);
            case "redo" -> redoSnorf.onCommand(sender, command, label, args);
            default -> {
                CommandHelper.sendError(sender, "Invalid subcommand: " + subcommand);
                yield false;
            }
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            String typed = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(cmd -> cmd.startsWith(typed))
                    .toList();
        }

        if (!(sender instanceof Player player)) return null;

        String subcommand = args[0].toLowerCase();

        if (subcommand.equals("count")) {
            return List.of(Material.values()).stream()
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .filter(name -> name.startsWith(args[args.length - 1].toLowerCase()))
                    .limit(20)
                    .toList();
        }

        if (args.length == 2) {
            switch (subcommand) {
                case "set", "fill", "center" -> {
                    return List.of(Material.values()).stream()
                            .filter(Material::isBlock)
                            .map(Material::name)
                            .map(String::toLowerCase)
                            .filter(name -> name.startsWith(args[1].toLowerCase()))
                            .limit(20)
                            .toList();
                }
                case "pos" -> {
                    return List.of("1", "2");
                }
                case "select" -> {
                    return List.of("2", "5", "10", "15", "20");
                }
            }

        }

        if ((subcommand.equals("replace") && (args.length == 2 || args.length == 3))) {
            return List.of(Material.values()).stream()
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .filter(name -> name.startsWith(args[args.length - 1].toLowerCase()))
                    .limit(20)
                    .toList();
        }

        if (subcommand.equals("pos") && args.length >= 3 && args.length <= 5) {
            Location loc = player.getLocation();
            switch (args.length) {
                case 3 -> {
                    return List.of(String.valueOf(loc.getBlockX()));
                }
                case 4 -> {
                    return List.of(String.valueOf(loc.getBlockY()));
                }
                case 5 -> {
                    return List.of(String.valueOf(loc.getBlockZ()));
                }
            }
        }

        return List.of();
    }
}
