package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static jinzo.terranite.listeners.SelectionListener.showOutline;

public class posTerra {
    public static boolean onCommand(@NotNull CommandSender sender,
                                    @NotNull Command command,
                                    @NotNull String label,
                                    @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length < 2) {
            CommandHelper.sendError(player, "Usage: /s pos <1|2> [x] [y] [z]");
            return false;
        }

        Location currentLoc = player.getLocation();
        int currentX = currentLoc.getBlockX();
        int currentY = currentLoc.getBlockY();
        int currentZ = currentLoc.getBlockZ();

        int posNum;
        try {
            posNum = Integer.parseInt(args[1]);
            if (posNum != 1 && posNum != 2) {
                CommandHelper.sendError(player, "Position must be 1 or 2");
                return false;
            }
        } catch (NumberFormatException e) {
            CommandHelper.sendError(player, "Position must be 1 or 2");
            return false;
        }

        int x, y, z;
        try {
            x = (args.length > 2) ? Integer.parseInt(args[2]) : currentX;
            y = (args.length > 3) ? Integer.parseInt(args[3]) : currentY;
            z = (args.length > 4) ? Integer.parseInt(args[4]) : currentZ;
        } catch (NumberFormatException e) {
            CommandHelper.sendError(player, "Coordinates must be numbers");
            return false;
        }

        Location targetBlock = player.getWorld().getBlockAt(x, y, z).getLocation();

        if (posNum == 1) {
            SelectionManager.setPos1(player, targetBlock);
            CommandHelper.sendSuccess(player,
                    String.format("Position 1 set to %d, %d, %d", x, y, z));
        } else {
            SelectionManager.setPos2(player, targetBlock);
            CommandHelper.sendSuccess(player,
                    String.format("Position 2 set to %d, %d, %d", x, y, z));
        }

        showOutline(player, targetBlock);
        CommandHelper.playSound(player, targetBlock);

        return true;
    }
}
