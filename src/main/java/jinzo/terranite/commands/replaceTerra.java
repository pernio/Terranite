package jinzo.terranite.commands;

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
            return true;
        }

        if (args.length < 3) {
            CommandHelper.sendError(player, "Usage: /s replace <target_block> <new_block>");
            return true;
        }

        Material target = Material.matchMaterial(args[1]);
        Material replacement = Material.matchMaterial(args[2]);

        if (target == null || !target.isBlock()) {
            CommandHelper.sendError(player, "Invalid target block: " + args[1]);
            return true;
        }

        if (replacement == null || !replacement.isBlock()) {
            CommandHelper.sendError(player, "Invalid replacement block: " + args[2]);
            return true;
        }

        int changed = CommandHelper.modifySelection(player, replacement, block -> block.getType() == target);

        if (changed == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
        } else if (changed == -2) {
            CommandHelper.sendError(player, "Selection too large!");
        } else {
            CommandHelper.sendSuccess(player, "Replaced " + changed + (changed == 1 ? " block" : " blocks") + " of " + target.name().toLowerCase() + " with " + replacement.name().toLowerCase() + ".");
        }

        return true;
    }
}
