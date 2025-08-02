package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import org.bukkit.ChatColor;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class terraCommand implements CommandExecutor, TabCompleter {

    private final Terranite plugin = Terranite.getInstance();
    private final pasteTerra pasteTerra;
    private final saveTerra saveTerra;
    private final deleteTerra deleteTerra;
    private final ConfigManager config;

    public static final List<String> SUBCOMMANDS = List.of(
            "wand", "pos", "copy", "cut", "paste",
            "select", "fill", "replace", "count", "center", "undo", "redo", "clear", "save", "generate", "config", "delete", "extend", "shrink", "break", "set"
    );
    private static final List<String> ADMINSUBCOMMANDS = List.of("config");

    private static final List<String> CONFIG_KEYS = List.of(
            "maxSelectionSize", "selectEffectColor", "outlineEffectColor", "outlineEffectSpeed", "selectSoundName",
            "playSound", "logNotifications", "hideSelectionWhenHoldingOtherItem", "clearSelectionAfterCommand",
            "commandCooldown", "safeDeleteSchematic", "deleteWandOnDrop", "deleteWandOnStore", "deleteWandOnPickup",
            "deleteWandOnShot", "allowMultipleWands", "blockedBlocks", "notifiedBlocks", "blockedMaterials", "notifiedMaterials"
    );

    public terraCommand(pasteTerra pasteTerra, saveTerra saveTerra, deleteTerra deleteTerra) {
        this.pasteTerra = pasteTerra;
        this.saveTerra = saveTerra;
        this.deleteTerra = deleteTerra;
        this.config = plugin.getConfiguration();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use Terranite commands.");
            return false;
        }

        String subcommand = args.length > 0 ? args[0].toLowerCase() : "";
        String subsub = args.length > 1 ? args[1].toLowerCase() : "";

        // Allow config reload commands even during lockdown
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
            case "set" -> result = setTerra.onCommand(sender, command, label, args);
            case "break" -> result = breakTerra.onCommand(sender, command, label, args);
            case "pos" -> result = posTerra.onCommand(sender, command, label, args);
            case "copy" -> result = copyTerra.onCommand(sender, command, label, args);
            case "cut" -> result = cutTerra.onCommand(sender, command, label, args);
            case "paste" -> result = pasteTerra.onCommand(sender, command, label, args);
            case "select" -> result = selectTerra.onCommand(sender, command, label, args);
            case "fill" -> result = fillTerra.onCommand(sender, command, label, args);
            case "replace" -> result = replaceTerra.onCommand(sender, command, label, args);
            case "count" -> result = countTerra.onCommand(sender, command, label, args);
            case "center" -> result = centerTerra.onCommand(sender, command, label, args);
            case "undo" -> result = undoTerra.onCommand(sender, command, label, args);
            case "redo" -> result = redoTerra.onCommand(sender, command, label, args);
            case "clear" -> result = clearTerra.onCommand(sender, command, label, args);
            case "save" -> result = saveTerra.onCommand(sender, command, label, args);
            case "delete" -> result = deleteTerra.onCommand(sender, command, label, args);
            case "generate" -> result = generateTerra.onCommand(sender, command, label, args);
            case "extend" -> result = extendTerra.onCommand(sender, command, label, args);
            case "shrink" -> result = shrinkTerra.onCommand(sender, command, label, args);
            case "config" -> {
                if (args.length >= 2) {
                    String configSub = args[1].toLowerCase();
                    switch (configSub) {
                        case "reload" -> {
                            if (!player.hasPermission("terranite.admin")) {
                                CommandHelper.sendError(player, "You do not have permission to reload Terranite.");
                                return false;
                            }
                            plugin.reloadConfig();
                            plugin.getConfiguration().reload();
                            CommandHelper.sendSuccess(player, "Terranite configuration reloaded.");
                            return true;
                        }
                        case "info" -> {
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
        if (args[0].equalsIgnoreCase("config")) {
            if (!player.hasPermission("terranite.admin")) return List.of();

            if (args.length == 2) {
                return Stream.of("reload", "info")
                        .filter(sub -> sub.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("info")) {
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
        if (subcommand.equals("count") || subcommand.equals("break")) {
            return Stream.of(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .filter(name -> name.startsWith(args[args.length - 1].toLowerCase()))
                    .limit(20)
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (subcommand) {
                case "set", "fill", "center" -> {
                    return Stream.of(Material.values())
                            .filter(Material::isBlock)
                            .filter(m -> exempt || inverted == blockedMaterials.contains(m))
                            .map(Material::name)
                            .map(String::toLowerCase)
                            .filter(name -> name.startsWith(args[1].toLowerCase()))
                            .limit(20)
                            .collect(Collectors.toList());
                }
                case "pos" -> {
                    return List.of("1", "2");
                }
                case "select" -> {
                    return List.of("2", "5", "10", "15", "20");
                }
                case "paste", "delete" -> {
                    String typed = args[1].toLowerCase();
                    List<String> allSchematics = plugin.getSchematicIO().getSavedSchematicNames();
                    return allSchematics.stream()
                            .filter(name -> name.toLowerCase().startsWith(typed))
                            .limit(20)
                            .collect(Collectors.toList());
                }
                case "generate" -> {
                    return List.of("box", "hollow_box", "sphere", "hollow_sphere");
                }
                case "extend", "shrink" -> {
                    return Stream.of("north", "south", "east", "west", "up", "down")
                            .filter(dir -> dir.startsWith(args[1].toLowerCase()))
                            .toList();
                }
            }
        }

        if (subcommand.equals("replace")) {
            if (args.length == 2) {
                return Stream.of(Material.values())
                        .filter(Material::isBlock)
                        .map(Material::name)
                        .map(String::toLowerCase)
                        .filter(name -> name.startsWith(args[1].toLowerCase()))
                        .limit(20)
                        .collect(Collectors.toList());
            }
            if (args.length == 3) {
                return Stream.of(Material.values())
                        .filter(Material::isBlock)
                        .filter(m -> exempt || inverted == blockedMaterials.contains(m))
                        .map(Material::name)
                        .map(String::toLowerCase)
                        .filter(name -> name.startsWith(args[2].toLowerCase()))
                        .limit(20)
                        .collect(Collectors.toList());
            }
        }

        if (subcommand.equals("generate") && args.length == 3) {
            return Stream.of(Material.values())
                    .filter(Material::isBlock)
                    .filter(m -> exempt || inverted == blockedMaterials.contains(m))
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

        if ((subcommand.equals("extend") || subcommand.equals("shrink")) && args.length == 3) {
            return Stream.of("1", "2", "5", "10", "20")
                    .filter(num -> num.startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return List.of();
    }

    // Helper to format a key-value pair with colors
    private String formatConfigEntry(String key, Object value) {
        String keyColored = ChatColor.WHITE + key + ":" + ChatColor.RESET + " ";
        String valueColored;

        if (value instanceof Boolean b) {
            valueColored = b ? ChatColor.DARK_GREEN + "true" : ChatColor.RED + "false";
        } else if (value instanceof Number || value instanceof Enum<?>) {
            valueColored = ChatColor.GRAY + value.toString();
        } else if (value instanceof String s) {
            valueColored = ChatColor.GRAY + s;
        } else {
            // For lists, sets or other objects fallback to plain string
            valueColored = ChatColor.GRAY + String.valueOf(value);
        }

        return keyColored + valueColored;
    }

    private void sendAllConfigInfo(Player player) {
        CommandHelper.sendInfo(player, ChatColor.GOLD + "Config Info:");
        CommandHelper.sendInfo(player, formatConfigEntry("maxSelectionSize", config.maxSelectionSize));
        CommandHelper.sendInfo(player, formatConfigEntry("selectEffectColor", config.selectEffectColor));
        CommandHelper.sendInfo(player, formatConfigEntry("outlineEffectColor", config.outlineEffectColor));
        CommandHelper.sendInfo(player, formatConfigEntry("outlineEffectSpeed", config.outlineEffectSpeed));
        CommandHelper.sendInfo(player, formatConfigEntry("selectSoundName", config.selectSoundName));
        CommandHelper.sendInfo(player, formatConfigEntry("playSound", config.playSound));
        CommandHelper.sendInfo(player, formatConfigEntry("logNotifications", config.logNotifications));
        CommandHelper.sendInfo(player, formatConfigEntry("hideSelectionWhenHoldingOtherItem", config.hideSelectionWhenHoldingOtherItem));
        CommandHelper.sendInfo(player, formatConfigEntry("clearSelectionAfterCommand", config.clearSelectionAfterCommand));
        CommandHelper.sendInfo(player, formatConfigEntry("commandCooldown (ms)", config.commandCooldown));
        CommandHelper.sendInfo(player, formatConfigEntry("safeDeleteSchematic", config.safeDeleteSchematic));
        CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnDrop", config.deleteWandOnDrop));
        CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnStore", config.deleteWandOnStore));
        CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnPickup", config.deleteWandOnPickup));
        CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnShot", config.deleteWandOnShot));
        CommandHelper.sendInfo(player, formatConfigEntry("allowMultipleWands", config.allowMultipleWands));
        CommandHelper.sendInfo(player, ChatColor.GRAY + "For listed block details: \"//config info blockedBlocks\"");
    }

    private void sendSpecificConfigInfo(Player player, String keyRaw) {
        CommandHelper.sendInfo(player, ChatColor.GOLD + "Config Info:");
        String key = keyRaw.toLowerCase();
        switch (key) {
            case "maxselectionsize" -> CommandHelper.sendInfo(player, formatConfigEntry("maxSelectionSize", config.maxSelectionSize));
            case "selecteffectcolor" -> CommandHelper.sendInfo(player, formatConfigEntry("selectEffectColor", config.selectEffectColor));
            case "outlineeffectcolor" -> CommandHelper.sendInfo(player, formatConfigEntry("outlineEffectColor", config.outlineEffectColor));
            case "outlineeffectspeed" -> CommandHelper.sendInfo(player, formatConfigEntry("outlineEffectSpeed", config.outlineEffectSpeed));
            case "selectsoundname" -> CommandHelper.sendInfo(player, formatConfigEntry("selectSoundName", config.selectSoundName));
            case "playsound" -> CommandHelper.sendInfo(player, formatConfigEntry("playSound", config.playSound));
            case "lognotifications" -> CommandHelper.sendInfo(player, formatConfigEntry("logNotifications", config.logNotifications));
            case "hideselectionwhenholdingotheritem" -> CommandHelper.sendInfo(player, formatConfigEntry("hideSelectionWhenHoldingOtherItem", config.hideSelectionWhenHoldingOtherItem));
            case "clearselectionaftercommand" -> CommandHelper.sendInfo(player, formatConfigEntry("clearSelectionAfterCommand", config.clearSelectionAfterCommand));
            case "commandcooldown" -> CommandHelper.sendInfo(player, formatConfigEntry("commandCooldown (ms)", config.commandCooldown));
            case "safedeletesechematic" -> CommandHelper.sendInfo(player, formatConfigEntry("safeDeleteSchematic", config.safeDeleteSchematic));
            case "deletewandondrop" -> CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnDrop", config.deleteWandOnDrop));
            case "deletewandonstore" -> CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnStore", config.deleteWandOnStore));
            case "deletewandonpickup" -> CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnPickup", config.deleteWandOnPickup));
            case "deletewandonshot" -> CommandHelper.sendInfo(player, formatConfigEntry("deleteWandOnShot", config.deleteWandOnShot));
            case "allowmultiplewands" -> CommandHelper.sendInfo(player, formatConfigEntry("allowMultipleWands", config.allowMultipleWands));
            case "blockedblocks" -> sendCompactList(player, "blockedBlocks", config.blockedBlocks);
            case "notifiedblocks" -> sendCompactList(player, "notifiedBlocks", config.notifiedBlocks);
            case "blockedmaterials" -> sendCompactSet(player, "blockedMaterials", config.blockedMaterials);
            case "notifiedmaterials" -> sendCompactSet(player, "notifiedMaterials", config.notifiedMaterials);
            default -> CommandHelper.sendError(player, "Unknown config key: " + keyRaw);
        }
    }

    private void sendCompactList(Player player, String name, List<?> list) {
        String value = list.isEmpty() ? ChatColor.WHITE + "(empty)" :
                ChatColor.GRAY + "\n- " + String.join("\n- ", list.stream().map(Object::toString).toList());
        CommandHelper.sendInfo(player, ChatColor.WHITE + name + ":" + ChatColor.RESET + " " + value);
    }

    private void sendCompactSet(Player player, String name, Set<?> set) {
        String value = set.isEmpty() ? ChatColor.WHITE + "(empty)" :
                ChatColor.GRAY + "\n- " + String.join("\n- ", set.stream().map(Object::toString).toList());
        CommandHelper.sendInfo(player, ChatColor.WHITE + name + ":" + ChatColor.RESET + " " + value);
    }
}
