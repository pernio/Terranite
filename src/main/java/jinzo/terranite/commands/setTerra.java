package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
            CommandHelper.sendError(player, "Usage: /s set <block>");
            return false;
        }

        Material material = Material.matchMaterial(args[1]);
        if (material == null || !material.isBlock()) {
            CommandHelper.sendError(player, "Invalid block type: " + args[1]);
            return false;
        }

        if (CommandHelper.checkMaterialBlocked(player, material)) return false;

        int changed = CommandHelper.modifySelection(player, material, block -> true);

        if (changed == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        } else if (changed == -2 || changed == -3) {
            return false;
        } else {
            CommandHelper.sendSuccess(player, "Set " + changed + (changed == 1 ? " block" : " blocks") + " to " + material.name().toLowerCase() + ".");
        }

        return true;
    }
}
