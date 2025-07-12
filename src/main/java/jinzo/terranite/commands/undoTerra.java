package jinzo.terranite.commands;

import jinzo.terranite.utils.ActionHistoryManager;
import jinzo.terranite.utils.CommandHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class undoTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (ActionHistoryManager.undo(player)) {
            CommandHelper.sendSuccess(player, "Undid your last action.");
        } else {
            CommandHelper.sendError(player, "No actions to undo.");
        }

        return true;
    }
}
