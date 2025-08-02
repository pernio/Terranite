package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class moveTerra {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this command.");
            return false;
        }

        if (args.length < 3) {
            CommandHelper.sendError(player, "Usage: //move <direction> <range>");
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

        int dx = 0, dy = 0, dz = 0;

        switch (direction) {
            case "north" -> dz = -range;
            case "south" -> dz = range;
            case "east" -> dx = range;
            case "west" -> dx = -range;
            case "up" -> dy = range;
            case "down" -> dy = -range;
            default -> {
                CommandHelper.sendError(player, "Invalid direction. Use: north, south, east, west, up, down.");
                return false;
            }
        }

        // Move both positions
        selection.pos1.add(dx, dy, dz);
        selection.pos2.add(dx, dy, dz);

        CommandHelper.sendSuccess(player, "Selection moved " + direction + " by " + range + (range == 1 ? " block." : " blocks."));
        return true;
    }
}
