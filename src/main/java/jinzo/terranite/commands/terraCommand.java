package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.commands.config.reloadTerra;
import jinzo.terranite.commands.schematic.deleteTerra;
import jinzo.terranite.commands.schematic.saveTerra;
import jinzo.terranite.commands.schematic.listTerra;
import jinzo.terranite.utils.CommandHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jinzo.terranite.utils.ConfigManager;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class terraCommand implements CommandExecutor, TabCompleter {

    private final Terranite plugin = Terranite.getInstance();
    private final pasteTerra pasteTerra;
    private final jinzo.terranite.commands.schematic.saveTerra saveTerra;
    private final jinzo.terranite.commands.schematic.deleteTerra deleteTerra;
    private final jinzo.terranite.commands.schematic.listTerra listTerra;
    private final ConfigManager config;

    public static final List<String> SUBCOMMANDS = List.of(
            "break", "center", "clear", "copy", "count",
            "cut", "extend", "fill", "generate", "paste", "pos",
            "redo", "replace", "replacenear", "select", "set",
            "shrink", "undo", "wand", "schematic", "move", "mask",
            "apply", "cancel", "teleport", "bind", "unbind"
    );
    private static final List<String> ADMINSUBCOMMANDS = List.of("config", "cfg");

    private static final List<String> CONFIG_KEYS = List.of(
            "maxSelectionSize", "selectEffectColor", "outlineEffectColor", "outlineEffectSpeed", "selectSoundName",
            "playSound", "logNotifications", "hideSelectionWhenHoldingOtherItem", "clearSelectionAfterCommand",
            "commandCooldown", "safeDeleteSchematic", "deleteWandOnDrop", "deleteWandOnStore", "deleteWandOnPickup",
            "deleteWandOnShot", "allowMultipleWands", "blockedBlocks", "notifiedBlocks", "blockedMaterials", "notifiedMaterials"
    );

    public terraCommand(pasteTerra pasteTerra, saveTerra saveTerra, deleteTerra deleteTerra, listTerra listTerra) {
        this.pasteTerra = pasteTerra;
        this.saveTerra = saveTerra;
        this.deleteTerra = deleteTerra;
        this.listTerra = listTerra;
        this.config = plugin.getConfiguration();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
                CommandHelper.sendError(sender, "Only players can use Terranite commands.");
                return false;
        }

        // Allow config reload commands even during lockdown
        String subcommand = args.length > 0 ? args[0].toLowerCase() : "";
        String subsub = args.length > 1 ? args[1].toLowerCase() : "";
        boolean isConfigReload = subcommand.equals("config") && subsub.equals("reload");


        if (plugin.getConfiguration().lockdown && !isConfigReload) {
            CommandHelper.sendError(player, "Terranite is currently in lockdown mode. Commands are disabled.");
            return false;
        }

        if (!player.hasPermission("terranite.use")) {
            CommandHelper.sendError(player, "You do not have permission to use Terranite.");
            return false;
        }

        if (args.length == 0) {
            CommandHelper.sendError(sender, "Usage: //<subcommand>");
            return false;
        }

        long cooldown = plugin.getConfiguration().commandCooldown;
        if (!player.hasPermission("terranite.exempt.cooldown") && cooldown > 0 && plugin.getCooldownManager().isOnCooldown(player.getUniqueId())) {
            long remainingMs = plugin.getCooldownManager().getRemaining(player.getUniqueId());
            String formatted = CommandHelper.formatDuration(remainingMs);
            CommandHelper.sendError(player, "You must wait " + formatted + " before executing a command again.");
            return true;
        }

        boolean result;

        switch (subcommand) {
            case "wand" -> result = wandTerra.onCommand(sender, command, label, args);
            case "set", "s" -> result = setTerra.onCommand(sender, command, label, args);
            case "break", "br" -> result = breakTerra.onCommand(sender, command, label, args);
            case "pos", "p" -> result = posTerra.onCommand(sender, command, label, args);
            case "copy", "co" -> result = copyTerra.onCommand(sender, command, label, args);
            case "cut", "cu" -> result = cutTerra.onCommand(sender, command, label, args);
            case "paste", "pa" -> result = pasteTerra.onCommand(sender, command, label, args);
            case "select", "se" -> result = selectTerra.onCommand(sender, command, label, args);
            case "fill", "f" -> result = fillTerra.onCommand(sender, command, label, args);
            case "replace", "re" -> result = replaceTerra.onCommand(sender, command, label, args);
            case "replacenear", "ren" -> result = replacenearTerra.onCommand(sender, command, label, args);
            case "count", "cnt" -> result = countTerra.onCommand(sender, command, label, args);
            case "center", "ce" -> result = centerTerra.onCommand(sender, command, label, args);
            case "undo", "u" -> result = undoTerra.onCommand(sender, command, label, args);
            case "redo", "r" -> result = redoTerra.onCommand(sender, command, label, args);
            case "clear", "c" -> result = clearTerra.onCommand(sender, command, label, args);
            case "generate", "g" -> result = generateTerra.onCommand(sender, command, label, args);
            case "extend", "e" -> result = extendTerra.onCommand(sender, command, label, args);
            case "shrink", "sh" -> result = shrinkTerra.onCommand(sender, command, label, args);
            case "move", "mo" -> result = moveTerra.onCommand(sender, command, label, args);
            case "mask", "ma" -> result = maskTerra.onCommand(sender, command, label, args);
            case "teleport", "tp" -> result = teleportTerra.onCommand(sender, command, label, args);
            case "bind", "b" -> result = bindTerra.onCommand(sender, command, label, args);
            case "unbind", "ub" -> result = unbindTerra.onCommand(sender, command, label, args);
            case "apply", "yes" -> result = applyTerra.onCommand(sender);
            case "cancel", "no" -> result = cancelTerra.onCommand(sender);
            case "config", "cfg" -> {
                if (args.length >= 2) {
                    String configSub = args[1].toLowerCase();
                    switch (configSub) {
                        case "reload", "r" -> {
                            return reloadTerra.onCommand(sender, command, label, args);
                        }
                        case "info", "i" -> {
                            if (!player.hasPermission("terranite.admin")) {
                                CommandHelper.sendError(player, "You do not have permission to view config info.");
                                return false;
                            }
                            if (args.length == 2) {
                                // show all info compact
                                sendAllConfigInfo(player);
                                return true;
                            } else if (args.length == 3) {
                                // show specific info key
                                sendSpecificConfigInfo(player, args[2]);
                                return true;
                            } else {
                                CommandHelper.sendError(player, "Usage: //config info [key]");
                                return false;
                            }
                        }
                        default -> {
                            CommandHelper.sendError(sender, "Unknown config subcommand: " + configSub);
                            return false;
                        }
                    }
                } else {
                    CommandHelper.sendError(sender, "Usage: //config <reload|info>");
                    return false;
                }
            }
            case "schematic", "sc" -> {
                if (args.length < 2) {
                    CommandHelper.sendError(sender, "Usage: //schematic <save|delete|list> <name>");
                    return false;
                }

                String subSubcommand = args[1].toLowerCase();
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);

                switch (subSubcommand) {
                    case "save", "s" -> result = saveTerra.onCommand(sender, command, label, subArgs);
                    case "delete", "d" -> result = deleteTerra.onCommand(sender, command, label, subArgs);
                    case "list", "l" -> result = listTerra.onCommand(sender, command, label, subArgs);
                    default -> {
                        CommandHelper.sendError(sender, "Unknown schematic subcommand: " + subSubcommand);
                        return false;
                    }
                }
                break;
            }

            default -> {
                CommandHelper.sendError(sender, "Invalid subcommand: " + subcommand);
                return false;
            }
        }

        // Apply cooldown only if command succeeded
        if (result && !player.hasPermission("terranite.exempt.cooldown") && cooldown > 0) {
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), cooldown);
        }

        return result;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) return List.of();
        if (!player.hasPermission("terranite.use")) return List.of();

        // Check if command is slash command or normal, then delegate to common handler
        String cmdName = command.getName();
        return completeCommand(player, cmdName, args);
    }

    // Extracted common logic for tab completion
    private List<String> completeCommand(Player player, String cmdName, String[] args) {
        var config = plugin.getConfiguration();
        Set<Material> blockedMaterials = config.blockedMaterials;
        boolean inverted = Terranite.getInstance().getConfiguration().excludeBlockedBlocks;
        boolean exempt = player.hasPermission("terranite.exempt.blockedBlocks");

        if (args.length == 0) return List.of();

        // Handle config subcommands first
        if (args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("cfg")) {
            if (!player.hasPermission("terranite.admin")) return List.of();

            if (args.length == 2) {
                return Stream.of("reload", "info", "r", "i")
                        .filter(sub -> sub.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 3 && (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("i"))) {
                return CONFIG_KEYS.stream()
                        .filter(key -> key.startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        String subcommand = args[0].toLowerCase();

        if (!cmdName.startsWith("/") && args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(cmd -> !ADMINSUBCOMMANDS.contains(cmd) || player.hasPermission("terranite.admin"))
                    .filter(cmd -> cmd.startsWith(subcommand))
                    .collect(Collectors.toList());
        }

        // Block-material based suggestions
        if (subcommand.equals("count") || subcommand.equals("cnt") || subcommand.equals("break") || subcommand.equals("br")) {
            return Stream.of(Material.values())
                    .filter(Material::isBlock)
                    .flatMap(material -> Stream.of(
                            material.name().toLowerCase(),                    // e.g., "stone"
                            "minecraft:" + material.name().toLowerCase()     // e.g., "minecraft:stone"
                    ))
                    .filter(name -> name.startsWith(args[args.length - 1].toLowerCase()))
                    .distinct()
                    .sorted(Comparator.comparing((String name) -> name.startsWith("minecraft:") ? 1 : 0)
                            .thenComparing(String::compareTo))
                    .limit(20)
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (subcommand) {
                case "set", "fill", "center", "s", "f", "ce" -> {
                    return Stream.of(Material.values())
                            .filter(Material::isBlock)
                            .filter(m -> exempt || inverted == blockedMaterials.contains(m))
                            .flatMap(material -> Stream.of(
                                    material.name().toLowerCase(),                    // e.g., "stone"
                                    "minecraft:" + material.name().toLowerCase()     // e.g., "minecraft:stone"
                            ))
                            .filter(name -> name.startsWith(args[1].toLowerCase()))
                            .distinct()
                            .sorted(Comparator.comparing((String name) -> name.startsWith("minecraft:") ? 1 : 0)
                                    .thenComparing(String::compareTo))
                            .limit(20)
                            .collect(Collectors.toList());
                }
                case "pos", "p" -> {
                    return List.of("1", "2");
                }
                case "select", "se" -> {
                    return List.of("2", "5", "10", "15", "20");
                }
                case "paste", "pa" -> {
                    String typed = args[1].toLowerCase();
                    List<String> allSchematics = plugin.getSchematicIO().getSavedSchematicNames();
                    return allSchematics.stream()
                            .filter(name -> name.toLowerCase().startsWith(typed))
                            .limit(20)
                            .collect(Collectors.toList());
                }
                case "generate", "g" -> {
                    return List.of("box", "hollow_box", "sphere", "hollow_sphere");
                }
                case "extend", "shrink", "move", "e", "sh", "mo" -> {
                    return Stream.of("north", "south", "east", "west", "up", "down", "n", "s", "e", "w", "u", "d")
                            .filter(dir -> dir.startsWith(args[1].toLowerCase()))
                            .toList();
                }
            }
        }

        if ((args[0].equalsIgnoreCase("schematic") || args[0].equalsIgnoreCase("sc"))) {
            if (args.length == 2) {
                return Stream.of("save", "delete", "list", "s", "d", "l")
                        .filter(sub -> sub.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (args.length == 3 && (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("d"))) {
                return plugin.getSchematicIO().getSavedSchematicNames().stream()
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .limit(20)
                        .collect(Collectors.toList());
            }
        }

        if (subcommand.equals("replacenear") || subcommand.equals("ren")) {
            if (args.length == 2) {
                return Stream.of("1", "2", "5", "10", "20")
                        .filter(num -> num.startsWith(args[1].toLowerCase()))
                        .toList();
            }
            if (args.length == 3) {
                return Stream.of(Material.values())
                        .filter(Material::isBlock)
                        .flatMap(material -> Stream.of(
                                material.name().toLowerCase(),                    // e.g., "stone"
                                "minecraft:" + material.name().toLowerCase()     // e.g., "minecraft:stone"
                        ))
                        .filter(name -> name.startsWith(args[2].toLowerCase()))
                        .distinct()
                        .sorted(Comparator.comparing((String name) -> name.startsWith("minecraft:") ? 1 : 0)
                                .thenComparing(String::compareTo))
                        .limit(20)
                        .collect(Collectors.toList());
            }
            if (args.length == 4) {
                return Stream.of(Material.values())
                        .filter(Material::isBlock)
                        .filter(m -> exempt || inverted == blockedMaterials.contains(m))
                        .flatMap(material -> Stream.of(
                                material.name().toLowerCase(),                    // e.g., "stone"
                                "minecraft:" + material.name().toLowerCase()     // e.g., "minecraft:stone"
                        ))
                        .filter(name -> name.startsWith(args[3].toLowerCase()))
                        .distinct()
                        .sorted(Comparator.comparing((String name) -> name.startsWith("minecraft:") ? 1 : 0)
                                .thenComparing(String::compareTo))
                        .limit(20)
                        .collect(Collectors.toList());
            }
        }

        if (subcommand.equals("replace") || subcommand.equals("re") || subcommand.equals("mask") || subcommand.equals("ma")) {
            if (args.length == 2) {
                return Stream.of(Material.values())
                        .filter(Material::isBlock)
                        .flatMap(material -> Stream.of(
                                material.name().toLowerCase(),                    // e.g., "stone"
                                "minecraft:" + material.name().toLowerCase()     // e.g., "minecraft:stone"
                        ))
                        .filter(name -> name.startsWith(args[1].toLowerCase()))
                        .distinct()
                        .sorted(Comparator.comparing((String name) -> name.startsWith("minecraft:") ? 1 : 0)
                                .thenComparing(String::compareTo))
                        .limit(20)
                        .collect(Collectors.toList());
            }
            if (args.length == 3 && !(subcommand.equals("mask") || subcommand.equals("ma"))) {
                return Stream.of(Material.values())
                        .filter(Material::isBlock)
                        .filter(m -> exempt || inverted == blockedMaterials.contains(m))
                        .flatMap(material -> Stream.of(
                                material.name().toLowerCase(),                    // e.g., "stone"
                                "minecraft:" + material.name().toLowerCase()     // e.g., "minecraft:stone"
                        ))
                        .filter(name -> name.startsWith(args[2].toLowerCase()))
                        .distinct()
                        .sorted(Comparator.comparing((String name) -> name.startsWith("minecraft:") ? 1 : 0)
                                .thenComparing(String::compareTo))
                        .limit(20)
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3) {
            if (subcommand.equals("generate") || subcommand.equals("g")) {
                return Stream.of(Material.values())
                        .filter(Material::isBlock)
                        .filter(m -> exempt || inverted == blockedMaterials.contains(m))
                        .flatMap(material -> Stream.of(
                                material.name().toLowerCase(),                    // e.g., "stone"
                                "minecraft:" + material.name().toLowerCase()     // e.g., "minecraft:stone"
                        ))
                        .filter(name -> name.startsWith(args[2].toLowerCase()))
                        .distinct()
                        .sorted(Comparator.comparing((String name) -> name.startsWith("minecraft:") ? 1 : 0)
                                .thenComparing(String::compareTo))
                        .limit(20)
                        .collect(Collectors.toList());
            }

            if (subcommand.equals("set") || subcommand.equals("s")) {
                return List.of("#preview");
            }
        }

        if ((subcommand.equals("pos") || subcommand.equals("p")) && args.length >= 3 && args.length <= 5) {
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

        if ((subcommand.equals("extend") || subcommand.equals("e") || subcommand.equals("shrink") || subcommand.equals("sh") || subcommand.equals("move") || subcommand.equals("mo")) && args.length == 3) {
            return Stream.of("1", "2", "5", "10", "20")
                    .filter(num -> num.startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return List.of();
    }

    // Helper to format a key-value pair with colors
    private Component formatConfigEntryComponent(String key, Object value) {
        Component keyComp = Component.text(key + ": ").color(NamedTextColor.WHITE);
        Component valueComp;

        if (value instanceof Boolean b) {
            valueComp = Component.text(String.valueOf(b), b ? NamedTextColor.DARK_GREEN : NamedTextColor.RED);
        } else {
            valueComp = Component.text(String.valueOf(value), NamedTextColor.GRAY);
        }

        return keyComp.append(valueComp);
    }

    private void sendAllConfigInfo(Player player) {
        CommandHelper.sendInfo(player, Component.text("Config Info:").color(NamedTextColor.GOLD));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("maxSelectionSize", config.maxSelectionSize));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("selectEffectColor", config.selectEffectColor));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("outlineEffectColor", config.outlineEffectColor));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("outlineEffectSpeed", config.outlineEffectSpeed));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("selectSoundName", config.selectSoundName));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("playSound", config.playSound));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("logNotifications", config.logNotifications));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("hideSelectionWhenHoldingOtherItem", config.hideSelectionWhenHoldingOtherItem));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("clearSelectionAfterCommand", config.clearSelectionAfterCommand));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("commandCooldown (ms)", config.commandCooldown));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("safeDeleteSchematic", config.safeDeleteSchematic));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnDrop", config.deleteWandOnDrop));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnStore", config.deleteWandOnStore));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnPickup", config.deleteWandOnPickup));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnShot", config.deleteWandOnShot));
        CommandHelper.sendInfo(player, formatConfigEntryComponent("allowMultipleWands", config.allowMultipleWands));
        CommandHelper.sendInfo(player, Component.text("For listed block details: \"//config info blockedBlocks\"").color(NamedTextColor.GRAY));
    }

    private void sendSpecificConfigInfo(Player player, String keyRaw) {
        CommandHelper.sendInfo(player, Component.text("Config Info:").color(NamedTextColor.GOLD));
        String key = keyRaw.toLowerCase();
        switch (key) {
            case "maxselectionsize" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("maxSelectionSize", config.maxSelectionSize));
            case "selecteffectcolor" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("selectEffectColor", config.selectEffectColor));
            case "outlineeffectcolor" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("outlineEffectColor", config.outlineEffectColor));
            case "outlineeffectspeed" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("outlineEffectSpeed", config.outlineEffectSpeed));
            case "selectsoundname" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("selectSoundName", config.selectSoundName));
            case "playsound" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("playSound", config.playSound));
            case "lognotifications" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("logNotifications", config.logNotifications));
            case "hideselectionwhenholdingotheritem" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("hideSelectionWhenHoldingOtherItem", config.hideSelectionWhenHoldingOtherItem));
            case "clearselectionaftercommand" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("clearSelectionAfterCommand", config.clearSelectionAfterCommand));
            case "commandcooldown" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("commandCooldown (ms)", config.commandCooldown));
            case "safedeletesechematic" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("safeDeleteSchematic", config.safeDeleteSchematic));
            case "deletewandondrop" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnDrop", config.deleteWandOnDrop));
            case "deletewandonstore" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnStore", config.deleteWandOnStore));
            case "deletewandonpickup" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnPickup", config.deleteWandOnPickup));
            case "deletewandonshot" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("deleteWandOnShot", config.deleteWandOnShot));
            case "allowmultiplewands" -> CommandHelper.sendInfo(player, formatConfigEntryComponent("allowMultipleWands", config.allowMultipleWands));
            case "blockedblocks" -> sendCompactList(player, "blockedBlocks", config.blockedBlocks);
            case "notifiedblocks" -> sendCompactList(player, "notifiedBlocks", config.notifiedBlocks);
            case "blockedmaterials" -> sendCompactSet(player, "blockedMaterials", config.blockedMaterials);
            case "notifiedmaterials" -> sendCompactSet(player, "notifiedMaterials", config.notifiedMaterials);
            default -> CommandHelper.sendError(player, "Unknown config key: " + keyRaw);
        }
    }

    private void sendCompactList(Player player, String name, List<?> list) {
        Component nameComp = Component.text(name + ": ").color(NamedTextColor.WHITE);
        Component valueComp = list.isEmpty() ? Component.text("(empty)").color(NamedTextColor.WHITE)
                : Component.text("\n- " + String.join("\n- ", list.stream().map(Object::toString).toList()), NamedTextColor.GRAY);
        CommandHelper.sendInfo(player, nameComp.append(valueComp));
    }

    private void sendCompactSet(Player player, String name, Set<?> set) {
        Component nameComp = Component.text(name + ": ").color(NamedTextColor.WHITE);
        Component valueComp = set.isEmpty() ? Component.text("(empty)").color(NamedTextColor.WHITE)
                : Component.text("\n- " + String.join("\n- ", set.stream().map(Object::toString).toList()), NamedTextColor.GRAY);
        CommandHelper.sendInfo(player, nameComp.append(valueComp));
    }
}