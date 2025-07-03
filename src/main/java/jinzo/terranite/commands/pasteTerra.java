package jinzo.terranite.commands;

import jinzo.terranite.utils.ClipboardManager;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class pasteTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return true;
        }

        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null) {
            CommandHelper.sendError(player, "You must set Position 1 before pasting.");
            return true;
        }

        if (!ClipboardManager.hasClipboard(player.getUniqueId())) {
            CommandHelper.sendError(player, "Clipboard is empty. Use /s copy or /s cut first.");
            return true;
        }

        var clipboardData = ClipboardManager.getClipboard(player.getUniqueId());
        Map<String, BlockData> clipboard = clipboardData.blocks();

        Location pasteOrigin = selection.pos1;

        for (Map.Entry<String, BlockData> entry : clipboard.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int dx = Integer.parseInt(parts[0]);
            int dy = Integer.parseInt(parts[1]);
            int dz = Integer.parseInt(parts[2]);

            Block target = player.getWorld().getBlockAt(
                    pasteOrigin.getBlockX() + dx,
                    pasteOrigin.getBlockY() + dy,
                    pasteOrigin.getBlockZ() + dz
            );
            target.setBlockData(entry.getValue());
        }

        CommandHelper.sendSuccess(player, "Pasted clipboard at Position 1.");
        return true;
    }
}
