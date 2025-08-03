package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import jinzo.terranite.utils.LegacyBlockHelper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

public class setTerra {
    public static boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length < 2) {
            CommandHelper.sendError(player, "Usage: //set <block> [#preview]");
            return false;
        }

        Material material = CommandHelper.findMaterial(player, args[1]);
        if (material == null) return false;

        if (CommandHelper.checkMaterialBlocked(player, material)) return false;

        boolean isPreview = args.length >= 3 && args[2].equalsIgnoreCase("#preview");

        int changed;
        if (isPreview) {
            changed = CommandHelper.previewSelection(player, material, block -> true);
            if (changed > 0) {
                CommandHelper.requestPreview(player, material, changed);
            }
        } else {
            changed = CommandHelper.modifySelection(player, material, block -> true);
            if (changed > 0) {
                CommandHelper.sendSuccess(player, "Set " + changed + (changed == 1 ? " block" : " blocks") + " to " + material.name().toLowerCase() + ".");
            }
        }

        if (changed == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        } else if (changed == -2 || changed == -3) {
            return false;
        }

        CommandHelper.checkClearSelection(player);
        return true;
    }
}
