package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class bindTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConfigManager config = Terranite.getInstance().getConfiguration();

        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (!player.hasPermission("terranite.bind")) return false;

        if (CommandHelper.checkMultipleWands(player)) return false;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            CommandHelper.sendError(player, "You must be holding an item to bind it!");
            return false;
        }

        if (CommandHelper.isTerraWand(item)) {
            CommandHelper.sendError(player, "This item is already bound!");
            return false;
        }

        try {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                CommandHelper.sendError(player, "Cannot bind this item.");
                return false;
            }

            NamespacedKey key = new NamespacedKey(Terranite.getInstance(), "terra_wand");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            if (!meta.hasDisplayName()) {
                meta.displayName(
                        Component.text(config.wandName, config.wandColor)
                                .decoration(TextDecoration.ITALIC, false)
                );
            }

            if (config.wandDescription != null && !config.wandDescription.isEmpty()) {
                meta.lore(List.of(Component.text(config.wandDescription, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
            }

            item.setItemMeta(meta);

            CommandHelper.sendSuccess(player, "Successfully bound this item to Terranite!");
            return true;

        } catch (Exception e) {
            sender.sendMessage(Component.text("Failed to bind item: " + e.getMessage(), NamedTextColor.RED));
            e.printStackTrace();
            return false;
        }
    }
}
