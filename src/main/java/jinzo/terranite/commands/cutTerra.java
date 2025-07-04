package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class cutTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return true;
        }

        copyTerra.onCommand(sender, command, label, args);

        String[] airArgs = {"set", "air"};
        setTerra.onCommand(sender, command, label, airArgs);

        CommandHelper.sendSuccess(player, "Cut selection.");
        return true;
    }
}
