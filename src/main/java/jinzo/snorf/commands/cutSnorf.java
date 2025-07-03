package jinzo.snorf.commands;

import jinzo.snorf.utils.CommandHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class cutSnorf {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return true;
        }

        // Copy the selection first
        copySnorf.onCommand(sender, command, label, args);

        // Then set the selection blocks to air (clear it)
        String[] airArgs = {"set", "air"};
        setSnorf.onCommand(sender, command, label, airArgs);

        CommandHelper.sendSuccess(player, "Cut selection.");
        return true;
    }
}
