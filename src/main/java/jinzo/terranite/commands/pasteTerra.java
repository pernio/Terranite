package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.ClipboardManager;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SchematicIO;
import jinzo.terranite.utils.SchematicIO.SchematicData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        // Get blocked materials list once for efficiency
        var blockedMaterials = Terranite.getInstance().getConfiguration().blockedMaterials;

        for (Map.Entry<String, BlockData> entry : blocksToPaste.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int dx = Integer.parseInt(parts[0]);
            int dy = Integer.parseInt(parts[1]);
            int dz = Integer.parseInt(parts[2]);

            Location blockLoc = pasteOrigin.clone().add(dx, dy, dz);
            BlockData blockData = entry.getValue();
            if (blockedMaterials.contains(blockData.getMaterial())) {
                continue;
            }

            Block targetBlock = blockLoc.getBlock();
            targetBlock.setBlockData(blockData, false);

            BlockState state = targetBlock.getState();
            state.update(true, false);
        }

        CommandHelper.sendSuccess(player, "Pasted from " + sourceName + " at your location.");
        return true;
    }
}
