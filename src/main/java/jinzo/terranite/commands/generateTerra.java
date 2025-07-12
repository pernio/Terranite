package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class generateTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length != 3) {
            CommandHelper.sendError(player, "Usage: /s generate <box|hollow_box|sphere|hollow_sphere> <block>");
            return false;
        }

        String shape = args[1].toLowerCase();
        Material material = Material.matchMaterial(args[2]);

        if (material == null || !material.isBlock()) {
            CommandHelper.sendError(player, "Invalid block type: " + args[2]);
            return false;
        }

        var config = Terranite.getInstance().getConfiguration();

        if (config.blockedMaterials.contains(material)) {
            CommandHelper.sendError(player, "This block is forbidden to use");
            return false;
        }

        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        }

        Location pos1 = selection.pos1;
        Location pos2 = selection.pos2;

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int volume = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        boolean selectionValidSize = CommandHelper.checkSelectionSize(player, volume);
        if (!selectionValidSize) return false;

        World world = player.getWorld();
        int changed = 0;

        switch (shape) {
            case "box" -> {
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            Block block = world.getBlockAt(x, y, z);
                            block.setType(material);
                            changed++;
                        }
                    }
                }
            }
            case "hollow_box" -> {
                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            boolean isEdge = x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
                            if (isEdge) {
                                Block block = world.getBlockAt(x, y, z);
                                block.setType(material);
                                changed++;
                            }
                        }
                    }
                }
            }
            case "sphere", "hollow_sphere" -> {
                int centerX = (minX + maxX) / 2;
                int centerY = (minY + maxY) / 2;
                int centerZ = (minZ + maxZ) / 2;

                int radiusX = (maxX - minX + 1) / 2;
                int radiusY = (maxY - minY + 1) / 2;
                int radiusZ = (maxZ - minZ + 1) / 2;
                int radius = Math.min(radiusX, Math.min(radiusY, radiusZ));

                double rSq = radius * radius;
                double innerRSq = (radius - 1) * (radius - 1);

                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    for (int y = centerY - radius; y <= centerY + radius; y++) {
                        for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                            double dx = x - centerX;
                            double dy = y - centerY;
                            double dz = z - centerZ;
                            double distanceSq = dx * dx + dy * dy + dz * dz;

                            boolean isInside = distanceSq <= rSq;
                            boolean isShell = shape.equals("hollow_sphere") ? distanceSq >= innerRSq : true;

                            if (isInside && isShell) {
                                Block block = world.getBlockAt(x, y, z);
                                block.setType(material);
                                changed++;
                            }
                        }
                    }
                }
            }
            default -> {
                CommandHelper.sendError(player, "Unsupported shape: " + shape);
                return false;
            }
        }

        CommandHelper.sendSuccess(player, "Generated " + shape + " with " + changed + " blocks.");
        return true;
    }
}
