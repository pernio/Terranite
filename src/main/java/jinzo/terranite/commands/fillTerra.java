package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.MaskManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class fillTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length < 2) {
            CommandHelper.sendError(player, "Usage: //fill <block>");
            return false;
        }

        Material mask = MaskManager.getMask(player);
        Material material = CommandHelper.findMaterial(player, args[1]);
        if (material == null) return false;

        if (CommandHelper.checkMaterialBlocked(player, material)) return false;

        int changed = CommandHelper.modifySelection(player, material, block -> block.getType() == mask);

        if (changed == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        } else if (changed == -2) {
            return false;
        } else {
            CommandHelper.checkClearSelection(player);
            CommandHelper.sendSuccess(player, "Filled " + changed + (changed == 1 ? " block" : " blocks") + " with " + material.name().toLowerCase() + ".");
        }

        return true;
    }
}
