package jinzo.terranite.commands.config;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class infoTerra implements CommandExecutor {
    private final ConfigManager config = Terranite.getInstance().getConfiguration();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length == 2) {
            // Show all config info compact
            sendAllInfo(player);
            return true;
        } else if (args.length == 3) {
            // Show specific info key
            sendSpecificInfo(player, args[2].toLowerCase());
            return true;
        } else {
            CommandHelper.sendError(player, "Usage: //config info [key]");
            return false;
        }
    }

    private void sendAllInfo(Player player) {
        CommandHelper.sendInfo(player, "Config Info:");
        CommandHelper.sendInfo(player, " maxSelectionSize: " + config.maxSelectionSize);
        CommandHelper.sendInfo(player, " selectEffectColor: " + config.selectEffectColor);
        CommandHelper.sendInfo(player, " outlineEffectColor: " + config.outlineEffectColor);
        CommandHelper.sendInfo(player, " outlineEffectSpeed: " + config.outlineEffectSpeed);
        CommandHelper.sendInfo(player, " selectSoundName: " + config.selectSoundName);
        CommandHelper.sendInfo(player, " playSound: " + config.playSound);
        CommandHelper.sendInfo(player, " logNotifications: " + config.logNotifications);
        CommandHelper.sendInfo(player, " hideSelectionWhenHoldingOtherItem: " + config.hideSelectionWhenHoldingOtherItem);
        CommandHelper.sendInfo(player, " clearSelectionAfterCommand: " + config.clearSelectionAfterCommand);
        CommandHelper.sendInfo(player, " commandCooldown (ms): " + config.commandCooldown);
        CommandHelper.sendInfo(player, " safeDeleteSchematic: " + config.safeDeleteSchematic);
        CommandHelper.sendInfo(player, " deleteWandOnDrop: " + config.deleteWandOnDrop);
        CommandHelper.sendInfo(player, " deleteWandOnStore: " + config.deleteWandOnStore);
        CommandHelper.sendInfo(player, " deleteWandOnPickup: " + config.deleteWandOnPickup);
        CommandHelper.sendInfo(player, " deleteWandOnShot: " + config.deleteWandOnShot);
        CommandHelper.sendInfo(player, " allowMultipleWands: " + config.allowMultipleWands);

        sendCompactList(player, "blockedBlocks", config.blockedBlocks);
        sendCompactList(player, "notifiedBlocks", config.notifiedBlocks);
        sendCompactSet(player, "blockedMaterials", config.blockedMaterials);
        sendCompactSet(player, "notifiedMaterials", config.notifiedMaterials);
    }

    private void sendSpecificInfo(Player player, String key) {
        switch (key) {
            case "maxselectionsize":
                CommandHelper.sendInfo(player, "maxSelectionSize: " + config.maxSelectionSize);
                break;
            case "selecteffectcolor":
                CommandHelper.sendInfo(player, "selectEffectColor: " + config.selectEffectColor);
                break;
            case "outlineeffectcolor":
                CommandHelper.sendInfo(player, "outlineEffectColor: " + config.outlineEffectColor);
                break;
            case "outlineeffectspeed":
                CommandHelper.sendInfo(player, "outlineEffectSpeed: " + config.outlineEffectSpeed);
                break;
            case "selectsoundname":
                CommandHelper.sendInfo(player, "selectSoundName: " + config.selectSoundName);
                break;
            case "playsound":
                CommandHelper.sendInfo(player, "playSound: " + config.playSound);
                break;
            case "lognotifications":
                CommandHelper.sendInfo(player, "logNotifications: " + config.logNotifications);
                break;
            case "hideselectionwhenholdingotheritem":
                CommandHelper.sendInfo(player, "hideSelectionWhenHoldingOtherItem: " + config.hideSelectionWhenHoldingOtherItem);
                break;
            case "clearselectionaftercommand":
                CommandHelper.sendInfo(player, "clearSelectionAfterCommand: " + config.clearSelectionAfterCommand);
                break;
            case "commandcooldown":
                CommandHelper.sendInfo(player, "commandCooldown (ms): " + config.commandCooldown);
                break;
            case "safedeletesechematic":
                CommandHelper.sendInfo(player, "safeDeleteSchematic: " + config.safeDeleteSchematic);
                break;
            case "deletewandondrop":
                CommandHelper.sendInfo(player, "deleteWandOnDrop: " + config.deleteWandOnDrop);
                break;
            case "deletewandonstore":
                CommandHelper.sendInfo(player, "deleteWandOnStore: " + config.deleteWandOnStore);
                break;
            case "deletewandonpickup":
                CommandHelper.sendInfo(player, "deleteWandOnPickup: " + config.deleteWandOnPickup);
                break;
            case "deletewandonshot":
                CommandHelper.sendInfo(player, "deleteWandOnShot: " + config.deleteWandOnShot);
                break;
            case "allowmultiplewands":
                CommandHelper.sendInfo(player, "allowMultipleWands: " + config.allowMultipleWands);
                break;
            case "blockedblocks":
                sendCompactList(player, "blockedBlocks", config.blockedBlocks);
                break;
            case "notifiedblocks":
                sendCompactList(player, "notifiedBlocks", config.notifiedBlocks);
                break;
            case "blockedmaterials":
                sendCompactSet(player, "blockedMaterials", config.blockedMaterials);
                break;
            case "notifiedmaterials":
                sendCompactSet(player, "notifiedMaterials", config.notifiedMaterials);
                break;
            default:
                CommandHelper.sendError(player, "Unknown config key: " + key);
        }
    }

    private void sendCompactList(Player player, String name, List<String> list) {
        if (list == null || list.isEmpty()) {
            CommandHelper.sendInfo(player, name + ": (empty)");
            return;
        }
        int total = list.size();
        List<String> firstItems = list.stream().limit(5).collect(Collectors.toList());
        String summary = String.join(", ", firstItems);
        if (total > 5) summary += ", ... (" + total + " total)";
        CommandHelper.sendInfo(player, name + ": " + summary);
    }

    private void sendCompactSet(Player player, String name, Set<?> set) {
        if (set == null || set.isEmpty()) {
            CommandHelper.sendInfo(player, name + ": (empty)");
            return;
        }
        int total = set.size();
        List<String> firstItems = set.stream().map(Object::toString).limit(5).collect(Collectors.toList());
        String summary = String.join(", ", firstItems);
        if (total > 5) summary += ", ... (" + total + " total)";
        CommandHelper.sendInfo(player, name + ": " + summary);
    }
}
