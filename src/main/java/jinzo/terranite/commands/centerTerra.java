package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class centerTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length != 2) {
            CommandHelper.sendError(player, "Usage: /s center <block>");
            return false;
        }

        Material material = Material.matchMaterial(args[1]);
        if (material == null || !material.isBlock()) {
            CommandHelper.sendError(player, "Invalid block type: " + args[1]);
            return false;
        }

        if (Terranite.getInstance().getConfiguration().blockedMaterials.contains(material)) {
            CommandHelper.sendError(player, "This block is forbidden to use");
            return false;
        }

        var selection = jinzo.terranite.utils.SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        }

        Location loc1 = selection.pos1;
        Location loc2 = selection.pos2;

        int centerX = (loc1.getBlockX() + loc2.getBlockX()) / 2;
        int centerY = (loc1.getBlockY() + loc2.getBlockY()) / 2;
        int centerZ = (loc1.getBlockZ() + loc2.getBlockZ()) / 2;

        Block centerBlock = player.getWorld().getBlockAt(centerX, centerY, centerZ);
        centerBlock.setType(material);

        CommandHelper.sendSuccess(player, "Placed " + material.name().toLowerCase() + " at the center of the selection.");
        return true;
    }
}
