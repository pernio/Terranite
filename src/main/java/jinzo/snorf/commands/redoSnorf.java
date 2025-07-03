package jinzo.snorf.commands;

import jinzo.snorf.utils.ActionHistoryManager;
import jinzo.snorf.utils.CommandHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class redoSnorf {
    public static boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return true;
        }

        boolean success = ActionHistoryManager.redo(player);
        if (success) {
            CommandHelper.sendSuccess(player, "Redid your last undone action.");
        } else {
            CommandHelper.sendError(player, "No actions to redo.");
        }

        return true;
    }
}
