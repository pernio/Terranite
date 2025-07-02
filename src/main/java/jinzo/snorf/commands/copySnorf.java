package jinzo.snorf.commands;

import jinzo.snorf.utils.ClipboardManager;
import jinzo.snorf.utils.CommandHelper;
import jinzo.snorf.utils.SelectionManager;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class copySnorf {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return true;
        }

        var sel = SelectionManager.getSelection(player);
        if (sel.pos1 == null || sel.pos2 == null) {
            CommandHelper.sendError(player, "Set both positions first.");
            return true;
        }

        var loc1 = sel.pos1.getLocation();
        var loc2 = sel.pos2.getLocation();

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        Map<String, BlockData> clipboard = new HashMap<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    var block = player.getWorld().getBlockAt(x, y, z);
                    String key = (x - minX) + "," + (y - minY) + "," + (z - minZ);
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
