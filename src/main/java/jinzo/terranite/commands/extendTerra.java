package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class extendTerra {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this command.");
            return false;
        }

        if (args.length < 3) {
            CommandHelper.sendError(player, "Usage: //extend <direction> <range>");
            return false;
        }

        String direction = args[1].toLowerCase();
        int range;

        try {
            range = Integer.parseInt(args[2]);
            if (range <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            CommandHelper.sendError(player, "Range must be a positive integer.");
            return false;
        }

        var selection = SelectionManager.getSelection(player);
        if (selection == null || selection.pos1 == null || selection.pos2 == null) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        }

        Location pos1 = selection.pos1;
        Location pos2 = selection.pos2;

        double x1 = pos1.getX();
        double y1 = pos1.getY();
        double z1 = pos1.getZ();

        double x2 = pos2.getX();
        double y2 = pos2.getY();
        double z2 = pos2.getZ();

        switch (direction) {
            case "north" -> {
                // North means smaller Z, so find which pos has smaller Z and subtract from it
                if (z1 < z2) pos1.setZ(z1 - range);
                else pos2.setZ(z2 - range);
            }
            case "south" -> {
                // South means bigger Z, add range to the larger Z position
                if (z1 > z2) pos1.setZ(z1 + range);
                else pos2.setZ(z2 + range);
            }
            case "east" -> {
                // East means bigger X
                if (x1 > x2) pos1.setX(x1 + range);
                else pos2.setX(x2 + range);
            }
            case "west" -> {
                // West means smaller X
                if (x1 < x2) pos1.setX(x1 - range);
                else pos2.setX(x2 - range);
            }
            case "up" -> {
                // Up means bigger Y
                if (y1 > y2) pos1.setY(y1 + range);
                else pos2.setY(y2 + range);
            }
            case "down" -> {
                // Down means smaller Y
                if (y1 < y2) pos1.setY(y1 - range);
                else pos2.setY(y2 - range);
            }
            default -> {
                CommandHelper.sendError(player, "Invalid direction. Use: north, south, east, west, up, down.");
                return false;
            }
        }

        CommandHelper.sendSuccess(player,"Selection extended " + direction + " by " + range + (range == 1 ? " block." : " blocks."));
        return true;
    }
}
