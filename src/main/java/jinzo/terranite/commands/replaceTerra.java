package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class replaceTerra {
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

        if (args.length < 3) {
            CommandHelper.sendError(player, "Usage: /s replace <target_block> <new_block>");
            return false;
        }

        Material target = CommandHelper.findMaterial(player, args[1]);
        if (target == null) return false;

        Material replacement = CommandHelper.findMaterial(player, args[2]);
        if (replacement == null) return false;

        if (CommandHelper.checkMaterialBlocked(player, replacement)) return false;

        int changed = CommandHelper.modifySelection(player, replacement, block -> block.getType() == target);

        if (changed == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        } else if (changed == -2) {
            return false;
        } else {
            CommandHelper.checkClearSelection(player);
            CommandHelper.sendSuccess(player, "Replaced " + changed + (changed == 1 ? " block" : " blocks") + " of " + target.name().toLowerCase() + " with " + replacement.name().toLowerCase() + ".");
        }

        return true;
    }
}
