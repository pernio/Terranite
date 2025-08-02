package jinzo.terranite;

import jinzo.terranite.utils.PreviewManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cancelTerra {
    public static boolean onCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this.");
            return false;
        }

        if (!PreviewManager.hasPreview(player)) {
            player.sendMessage("You have no active preview.");
            return false;
        }

        PreviewManager.cancel(player);
        player.sendMessage("Preview cancelled.");
        return true;
    }
}
