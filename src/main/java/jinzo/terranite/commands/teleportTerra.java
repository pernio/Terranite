package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class teleportTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (!player.hasPermission("terranite.teleport")) return false;

        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        }

        Location loc1 = selection.pos1;
        Location loc2 = selection.pos2;

        if (loc1.getWorld() == null || loc2.getWorld() == null || !loc1.getWorld().equals(loc2.getWorld())) {
            CommandHelper.sendError(player, "Selection positions must be in the same world.");
            return false;
        }

        int centerX = (loc1.getBlockX() + loc2.getBlockX()) / 2;
        int centerY = (loc1.getBlockY() + loc2.getBlockY()) / 2;
        int centerZ = (loc1.getBlockZ() + loc2.getBlockZ()) / 2;

        Location target = new Location(loc1.getWorld(), centerX + 0.5, centerY, centerZ + 0.5);
        target.setYaw(player.getLocation().getYaw());
        target.setPitch(player.getLocation().getPitch());

        try {
            player.teleportAsync(target).thenAccept(success -> {
                // Run messaging back on the main server thread to be safe
                Bukkit.getScheduler().runTask(Terranite.getInstance(), () -> {
                    if (success) {
                        CommandHelper.sendSuccess(player, "Teleported to the center of the selection.");
                    } else {
                        CommandHelper.sendError(player, "Teleport failed.");
                    }
                });
            });
            return true;
        } catch (UnsupportedOperationException ex) {
            Bukkit.getScheduler().runTask(Terranite.getInstance(), () -> {
                try {
                    player.teleport(target); // will run on main thread
                    CommandHelper.sendSuccess(player, "Teleported to the center of the selection.");
                } catch (Exception e) {
                    CommandHelper.sendError(player, "Teleport failed: " + e.getMessage());
                }
            });
            return true;
        }
    }
}
