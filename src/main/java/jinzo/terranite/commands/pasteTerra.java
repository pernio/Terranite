package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.*;
import jinzo.terranite.utils.SchematicIO.SchematicData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class pasteTerra {
    private final SchematicIO schematicIO;

    public pasteTerra(SchematicIO schematicIO) {
        this.schematicIO = schematicIO;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        Map<String, BlockData> blocksToPaste;
        String sourceName;

        if (args.length >= 2) {
            String name = args[1];
            SchematicData schematicData = schematicIO.loadSchematic(name);
            if (schematicData == null) {
                CommandHelper.sendError(player, "Schematic '" + name + "' not found.");
                return false;
            }
            blocksToPaste = schematicData.blocks;
            sourceName = "schematic '" + name + "'";
        } else {
            if (!ClipboardManager.hasClipboard(player.getUniqueId())) {
                CommandHelper.sendError(player, "Clipboard is empty. Use /s copy or /s cut first.");
                return false;
            }
            var clipboardData = ClipboardManager.getClipboard(player.getUniqueId());
            blocksToPaste = clipboardData.blocks();
            sourceName = "clipboard";
        }

        Location pasteOrigin = player.getLocation().getBlock().getLocation();

        var blockedMaterials = Terranite.getInstance().getConfiguration().blockedMaterials;
        var notifiedMaterials = Terranite.getInstance().getConfiguration().notifiedMaterials;

        Map<Location, Material> snapshot = new HashMap<>();
        Map<Material, Integer> notifiedCount = new HashMap<>();
        Map<Material, Location> firstLocation = new HashMap<>();

        int changed = 0;

        for (Map.Entry<String, BlockData> entry : blocksToPaste.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int dx = Integer.parseInt(parts[0]);
            int dy = Integer.parseInt(parts[1]);
            int dz = Integer.parseInt(parts[2]);

            Location blockLoc = pasteOrigin.clone().add(dx, dy, dz);
            BlockData blockData = entry.getValue();

            if (!player.hasPermission("terranite.exempt.blockedBlocks") && blockedMaterials.contains(blockData.getMaterial())) {
                continue;
            }

            Block targetBlock = blockLoc.getBlock();
            Material before = targetBlock.getType();

            if (!before.equals(blockData.getMaterial()) || !targetBlock.getBlockData().matches(blockData)) {
                // Save original material for undo
                snapshot.put(blockLoc, before);

                // Set new block data without physics (false)
                targetBlock.setBlockData(blockData, false);

                // Update block state (optional but good)
                BlockState state = targetBlock.getState();
                state.update(true, false);

                changed++;

                // Check if this material should notify
                Material mat = blockData.getMaterial();
                if (notifiedMaterials.contains(mat)) {
                    notifiedCount.merge(mat, 1, Integer::sum);
                    firstLocation.putIfAbsent(mat, blockLoc);
                }
            }
        }

        if (!snapshot.isEmpty()) {
            ActionHistoryManager.record(player, snapshot);
        }

        // Bypass logging
        if (player.hasPermission("terranite.exempt.notifiedBlocks")) return true;

        // Log notifications for notified materials
        for (Map.Entry<Material, Integer> entry : notifiedCount.entrySet()) {
            Material mat = entry.getKey();
            int count = entry.getValue();
            Location loc = firstLocation.get(mat);

            String message = String.format(
                    "[Terra] %s placed %d blocks of %s (example at %d,%d,%d in %s)",
                    player.getName(),
                    count,
                    mat.name(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                    loc.getWorld().getName()
            );
            CommandHelper.logMessage(player, message);
        }

        CommandHelper.sendSuccess(player, "Pasted from " + sourceName + " at your location. Changed " + changed + " blocks.");
        return true;
    }
}
