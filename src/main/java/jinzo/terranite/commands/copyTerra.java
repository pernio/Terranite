package jinzo.terranite.commands;

import jinzo.terranite.utils.ClipboardManager;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class copyTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        var sel = SelectionManager.getSelection(player);
        if (sel.pos1 == null || sel.pos2 == null) {
            CommandHelper.sendError(player, "Set both positions first.");
            return false;
        }

        var loc1 = sel.pos1;
        var loc2 = sel.pos2;

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int volume = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        boolean selectionValidSize = CommandHelper.checkSelectionSize(player, volume);
        if (!selectionValidSize) return false;

        Map<String, BlockData> clipboard = new HashMap<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    int dx = x - player.getLocation().getBlockX();
                    int dy = y - player.getLocation().getBlockY();
                    int dz = z - player.getLocation().getBlockZ();
                    String key = dx + "," + dy + "," + dz;

                    clipboard.put(key, block.getBlockData());
                }
            }
        }

        ClipboardManager.setClipboard(
                player.getUniqueId(),
                clipboard,
                maxX - minX + 1,
                maxY - minY + 1,
                maxZ - minZ + 1,
                loc1,
                player.getLocation().getYaw(),
                player.getLocation().getPitch()
        );

        CommandHelper.sendSuccess(player, "Copied selection to clipboard.");
        return true;
    }
}
