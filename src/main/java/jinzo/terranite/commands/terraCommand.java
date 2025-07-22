package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
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
import java.util.Set;

public class terraCommand implements CommandExecutor, TabCompleter {

    private final pasteTerra pasteTerra;
    private final saveTerra saveTerra;

    private static final List<String> SUBCOMMANDS = List.of(
            "wand", "set", "break", "pos", "copy", "cut", "paste",
            "select", "fill", "replace", "count", "center", "undo", "redo", "clear", "save", "generate", "reload"
    );

    public terraCommand(pasteTerra pasteTerra, saveTerra saveTerra) {
        this.pasteTerra = pasteTerra;
        this.saveTerra = saveTerra;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use Terranite commands.");
            return false;
        }

        if (!player.hasPermission("terranite.use")) {
            CommandHelper.sendError(player, "You do not have permission to use Terranite.");
            return false;
        }
        if (args.length == 0) {
            CommandHelper.sendError(sender, "Usage: /s <subcommand>");
            return false;
        }

        String subcommand = args[0].toLowerCase();

        return switch (subcommand) {
            case "wand" -> wandTerra.onCommand(sender, command, label, args);
            case "set" -> setTerra.onCommand(sender, command, label, args);
            case "break" -> breakTerra.onCommand(sender, command, label, args);
            case "pos" -> posTerra.onCommand(sender, command, label, args);
            case "copy" -> copyTerra.onCommand(sender, command, label, args);
            case "cut" -> cutTerra.onCommand(sender, command, label, args);
            case "paste" -> pasteTerra.onCommand(sender, command, label, args);
            case "select" -> selectTerra.onCommand(sender, command, label, args);
            case "fill" -> fillTerra.onCommand(sender, command, label, args);
            case "replace" -> replaceTerra.onCommand(sender, command, label, args);
            case "count" -> countTerra.onCommand(sender, command, label, args);
            case "center" -> centerTerra.onCommand(sender, command, label, args);
            case "undo" -> undoTerra.onCommand(sender, command, label, args);
            case "redo" -> redoTerra.onCommand(sender, command, label, args);
            case "clear" -> clearTerra.onCommand(sender, command, label, args);
            case "save" -> saveTerra.onCommand(sender, command, label, args);
            case "generate" -> generateTerra.onCommand(sender, command, label, args);
            case "reload" -> {
                if (!player.hasPermission("terranite.admin")) {
                    CommandHelper.sendError(player, "You do not have permission to reload Terranite.");
                    yield false;
                }

                Terranite plugin = Terranite.getInstance();
                plugin.reloadConfig();
                plugin.getConfiguration().reload();

                CommandHelper.sendSuccess(player, "Terranite configuration reloaded.");
                yield true;
            }
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
        if (!(sender instanceof Player player) || !player.hasPermission("terranite.use")) {
            return List.of();
        }

        var config = Terranite.getInstance().getConfiguration();
        Set<Material> blockedMaterials = config.blockedMaterials;
        boolean exempt = player.hasPermission("terranite.exempt.blockedBlocks");

        if (args.length == 1) {
            String typed = args[0].toLowerCase();

            return SUBCOMMANDS.stream()
                    .filter(cmd -> !cmd.equals("reload") || player.hasPermission("terranite.admin"))
                    .filter(cmd -> cmd.startsWith(typed))
                    .toList();
        }

        String subcommand = args[0].toLowerCase();

        if (subcommand.equals("count") || subcommand.equals("break")) {
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
                            .filter(m -> exempt || !blockedMaterials.contains(m))
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
                case "paste" -> {
                    String typed = args[1].toLowerCase();
                    List<String> allSchematics = Terranite.getInstance().getSchematicIO().getSavedSchematicNames();
                    return allSchematics.stream()
                            .filter(name -> name.toLowerCase().startsWith(typed))
                            .limit(20)
                            .toList();
                }
                case "generate" -> {
                    return List.of("box", "hollow_box", "sphere", "hollow_sphere");
                }
            }

        }

        if (subcommand.equals("replace")) {
            if (args.length == 2) {
                return List.of(Material.values()).stream()
                        .filter(Material::isBlock)
                        .map(Material::name)
                        .map(String::toLowerCase)
                        .filter(name -> name.startsWith(args[args.length - 1].toLowerCase()))
                        .limit(20)
                        .toList();
            }

            if (args.length == 3) {
                return List.of(Material.values()).stream()
                        .filter(Material::isBlock)
                        .filter(m -> exempt || !blockedMaterials.contains(m))
                        .map(Material::name)
                        .map(String::toLowerCase)
                        .filter(name -> name.startsWith(args[args.length - 1].toLowerCase()))
                        .limit(20)
                        .toList();
            }
        }

        if (subcommand.equals("generate") && args.length == 3) {
            return List.of(Material.values()).stream()
                    .filter(Material::isBlock)
                    .filter(m -> exempt || !blockedMaterials.contains(m))
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .filter(name -> name.startsWith(args[2].toLowerCase()))
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
