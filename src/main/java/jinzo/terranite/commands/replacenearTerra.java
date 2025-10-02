package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class replacenearTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (!player.hasPermission("terranite.use")) {
            CommandHelper.sendError(player, "You do not have permission to use Terranite.");
            return false;
        }

        if (args.length < 4) {
            CommandHelper.sendError(player, "Usage: //replacenear <radius> <from> <to>");
            return false;
        }

        int radius;
        try {
            radius = Integer.parseInt(args[1]);
            if (radius < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            CommandHelper.sendError(player, "Invalid radius: " + args[1]);
            return false;
        }

        Material target = CommandHelper.findMaterial(player, args[2]);
        if (target == null) {
            CommandHelper.sendError(player, "Invalid 'from' material: " + args[2]);
            return false;
        }

        Material replacement = CommandHelper.findMaterial(player, args[3]);
        if (replacement == null) {
            CommandHelper.sendError(player, "Invalid 'to' material: " + args[3]);
            return false;
        }

        if (CommandHelper.checkMaterialBlocked(player, replacement)) return false;

        Location center = player.getLocation().getBlock().getLocation();

        Location minPos = new Location(center.getWorld(),
                center.getBlockX() - radius,
                center.getBlockY() - radius,
                center.getBlockZ() - radius);

        Location maxPos = new Location(center.getWorld(),
                center.getBlockX() + radius,
                center.getBlockY() + radius,
                center.getBlockZ() + radius);

        int changed = CommandHelper.modifySelection(player, replacement, block -> block.getType() == target, minPos, maxPos);

        if (changed == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        } else if (changed == -2) {
            return false;
        } else {
            CommandHelper.checkClearSelection(player);

            CommandHelper.sendSuccess(player, "Replaced " + changed + " blocks within radius " + radius +
                    " (" + target.name().toLowerCase() + " -> " + replacement.name().toLowerCase() + ").");
        }

        return true;
    }
}
