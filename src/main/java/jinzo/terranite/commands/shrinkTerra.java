package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class shrinkTerra {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this command.");
            return false;
        }

        if (args.length < 3) {
            CommandHelper.sendError(player, "Usage: //shrink <direction> <range>");
            return false;
        }

        String direction = "north";

        switch (args[1].toLowerCase()) {
            case "n" -> {
                direction = "north";
            }
            case "s" -> {
                direction = "south";
            }
            case "e" -> {
                direction = "east";
            }
            case "w" -> {
                direction = "west";
            }
            case "u" -> {
                direction = "up";
            }
            case "d" -> {
                direction = "down";
            }
        }

        int requested;

        try {
            requested = Integer.parseInt(args[2]);
            if (requested <= 0) throw new NumberFormatException();
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

        int actualShrunk = requested;

        switch (direction) {
            case "north" -> {
                double distance = Math.abs(z1 - z2);
                actualShrunk = Math.min(actualShrunk, (int) distance);
                if (z1 < z2) pos1.setZ(z1 + actualShrunk);
                else pos2.setZ(z2 + actualShrunk);
            }
            case "south" -> {
                double distance = Math.abs(z1 - z2);
                actualShrunk = Math.min(actualShrunk, (int) distance);
                if (z1 > z2) pos1.setZ(z1 - actualShrunk);
                else pos2.setZ(z2 - actualShrunk);
            }
            case "east" -> {
                double distance = Math.abs(x1 - x2);
                actualShrunk = Math.min(actualShrunk, (int) distance);
                if (x1 > x2) pos1.setX(x1 - actualShrunk);
                else pos2.setX(x2 - actualShrunk);
            }
            case "west" -> {
                double distance = Math.abs(x1 - x2);
                actualShrunk = Math.min(actualShrunk, (int) distance);
                if (x1 < x2) pos1.setX(x1 + actualShrunk);
                else pos2.setX(x2 + actualShrunk);
            }
            case "up" -> {
                double distance = Math.abs(y1 - y2);
                actualShrunk = Math.min(actualShrunk, (int) distance);
                if (y1 > y2) pos1.setY(y1 - actualShrunk);
                else pos2.setY(y2 - actualShrunk);
            }
            case "down" -> {
                double distance = Math.abs(y1 - y2);
                actualShrunk = Math.min(actualShrunk, (int) distance);
                if (y1 < y2) pos1.setY(y1 + actualShrunk);
                else pos2.setY(y2 + actualShrunk);
            }
            default -> {
                CommandHelper.sendError(player, "Invalid direction. Use: north, south, east, west, up, down.");
                return false;
            }
        }

        if (actualShrunk <= 0) {
            CommandHelper.sendError(player, "Selection is too small to shrink further in that direction.");
            return false;
        }

        if (actualShrunk < requested) {
            CommandHelper.sendSuccess(player, "Selection shrunk " + direction + " by " + actualShrunk + (actualShrunk == 1 ? " block" : " blocks") + " (requested " + requested + ").");
        } else {
            CommandHelper.sendSuccess(player, "Selection shrunk " + direction + " by " + actualShrunk + (actualShrunk == 1 ? " block" : " blocks"));
        }

        return true;
    }
}
