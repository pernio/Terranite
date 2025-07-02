package jinzo.snorf.commands;

import jinzo.snorf.Snorf;
import jinzo.snorf.utils.CommandHelper;
import jinzo.snorf.utils.ConfigManager;
import jinzo.snorf.utils.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class fillSnorf {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return true;
        }

        if (args.length < 2) {
            CommandHelper.sendError(sender, "Usage: /s fill <block>");
            return true;
        }

        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return true;
        }

        Material material = Material.matchMaterial(args[1]);
        if (material == null || !material.isBlock()) {
            CommandHelper.sendError(player, "Invalid block type: " + args[1]);
            return true;
        }

        var loc1 = selection.pos1.getLocation();
        var loc2 = selection.pos2.getLocation();

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        var world = player.getWorld();
        int count = 0;

        ConfigManager config = Snorf.getInstance().getConfiguration();
        int maxSelectionSize = config.maxSelectionSize;
        int totalBlocks = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        if (totalBlocks > maxSelectionSize) {
            CommandHelper.sendError(player, "Selection too large! (over " + maxSelectionSize + " blocks)");
            return true;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(material);
                    count++;
                }
            }
        }

        CommandHelper.sendSuccess(player, "Filled " + count + " block(s) with " + material.name().toLowerCase() + ".");
        return true;
    }
}