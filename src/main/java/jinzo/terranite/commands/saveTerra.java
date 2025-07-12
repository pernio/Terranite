package jinzo.terranite.commands;

import jinzo.terranite.utils.ClipboardManager;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SchematicIO;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class saveTerra {
    private final SchematicIO schematicIO;

    public saveTerra(SchematicIO schematicIO) {
        this.schematicIO = schematicIO;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length < 2) {
            CommandHelper.sendError(player, "Usage: /s save <name>");
            return false;
        }

        String name = args[1];

        if (!ClipboardManager.hasClipboard(player.getUniqueId())) {
            CommandHelper.sendError(player, "Clipboard is empty. Use /s copy or /s cut first.");
            return false;
        }

        var clipboardData = ClipboardManager.getClipboard(player.getUniqueId());
        Map<String, BlockData> blocks = clipboardData.blocks();

        boolean success = schematicIO.saveSchematic(
                name,
                blocks,
                clipboardData.origin()
        );
        if (success) {
            CommandHelper.sendSuccess(player, "Schematic saved as '" + name + "'");
        } else {
            CommandHelper.sendError(player, "Schematic name already exists or error occurred.");
            return false;
        }
        return true;
    }
}
