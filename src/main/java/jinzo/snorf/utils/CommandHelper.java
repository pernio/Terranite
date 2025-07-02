package jinzo.snorf.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class CommandHelper {
    public static void sendSuccess(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Snorf] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.DARK_GREEN);
            sender.sendMessage(prefix.append(body));
        }
    }

    public static void sendError(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Snorf] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.RED);
            sender.sendMessage(prefix.append(body));
        }
    }

    public static void sendInfo(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Snorf] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.YELLOW);
            sender.sendMessage(prefix.append(body));
        }
    }
}
