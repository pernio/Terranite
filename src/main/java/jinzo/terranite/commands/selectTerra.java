package jinzo.terranite.commands;

import jinzo.terranite.utils.SelectionManager;
import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class selectTerra {
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

        if (args.length != 2 || !args[0].equalsIgnoreCase("select")) {
            CommandHelper.sendError(player, "Usage: /s select <radius>");
            return false;
        }

        int radius;
        try {
            radius = Integer.parseInt(args[1]);
            if (radius < 0) {
                CommandHelper.sendError(player, "Radius must be a positive integer.");
                return false;
            }
        } catch (NumberFormatException e) {
            CommandHelper.sendError(player, "Invalid radius value.");
            return false;
        }

        Block center = player.getLocation().getBlock();

        Location pos1 = center.getWorld().getBlockAt(
                center.getX() - radius,
                Math.max(center.getY() - radius, 0),
                center.getZ() - radius
        ).getLocation();

        Location pos2 = center.getWorld().getBlockAt(
                center.getX() + radius,
                Math.min(center.getY() + radius, center.getWorld().getMaxHeight()),
                center.getZ() + radius
        ).getLocation();

        SelectionManager.setPos1(player, pos1);
        SelectionManager.setPos2(player, pos2);

        CommandHelper.sendSuccess(player, String.format("Selected box from (%d, %d, %d) to (%d, %d, %d)",
                (int) pos1.getX(), (int) pos1.getY(), (int) pos1.getZ(),
                (int) pos2.getX(), (int) pos2.getY(), (int) pos2.getZ()));

        return true;
    }
}
