package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class setTerra {

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

        if (args.length < 2) {
            CommandHelper.sendError(player, "Usage: //set <block>");
            return false;
        }

        Material material = CommandHelper.findMaterial(player, args[1]);
        if (material == null) return false;

        if (CommandHelper.checkMaterialBlocked(player, material)) return false;

        // Get the opposite of player's facing direction
        BlockFace facing = getPlayerFacing(player).getOppositeFace();

        int changed = CommandHelper.modifySelection(player, material, block -> {
            block.setType(material);
            BlockData data = block.getBlockData();

            // Face directional blocks toward the player
            if (data instanceof Directional directional) {
                if (directional.getFaces().contains(facing)) {
                    directional.setFacing(facing);
                    block.setBlockData(directional);
                }
            } else if (data instanceof Rotatable rotatable) {
                rotatable.setRotation(facing);
                block.setBlockData(rotatable);
            } else if (data instanceof Orientable orientable) {
                // Best effort: try to align with facing axis
                switch (facing) {
                    case NORTH, SOUTH -> orientable.setAxis(org.bukkit.Axis.Z);
                    case EAST, WEST -> orientable.setAxis(org.bukkit.Axis.X);
                    case UP, DOWN -> orientable.setAxis(org.bukkit.Axis.Y);
                }
                block.setBlockData(orientable);
            } else if (data instanceof MultipleFacing multi) {
                for (BlockFace face : multi.getAllowedFaces()) {
                    multi.setFace(face, true);
                } // face all directions (fallback)
                block.setBlockData(multi);
            }

            return true;
        });

        if (changed == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        } else if (changed == -2 || changed == -3) {
            return false;
        } else {
            CommandHelper.checkClearSelection(player);
            CommandHelper.sendSuccess(player, "Set " + changed + (changed == 1 ? " block" : " blocks") + " to " + material.name().toLowerCase() + ".");
        }

        return true;
    }

    private static BlockFace getPlayerFacing(Player player) {
        Vector dir = player.getLocation().getDirection();
        double yaw = Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));

        yaw = (yaw + 360) % 360;

        if (yaw < 45) return BlockFace.NORTH;
        if (yaw < 135) return BlockFace.EAST;
        if (yaw < 225) return BlockFace.SOUTH;
        if (yaw < 315) return BlockFace.WEST;
        return BlockFace.NORTH;
    }
}
