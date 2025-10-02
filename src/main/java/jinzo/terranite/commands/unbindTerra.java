package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class unbindTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (!player.hasPermission("terranite.bind")) return false;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            CommandHelper.sendError(player, "You must be holding an item to unbind it!");
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            CommandHelper.sendError(player, "This item cannot be unbound.");
            return false;
        }

        NamespacedKey key = new NamespacedKey(Terranite.getInstance(), "terra_wand");

        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            CommandHelper.sendError(player, "This item is not bound to Terranite.");
            return false;
        }

        try {
            meta.displayName(null);
            meta.lore(null);

            meta.getPersistentDataContainer().remove(key);
            item.setItemMeta(meta);

            CommandHelper.sendSuccess(player, "Successfully unbound this item from Terranite!");
            return true;

        } catch (Exception e) {
            sender.sendMessage(Component.text("Failed to unbind item: " + e.getMessage(), NamedTextColor.RED));
            e.printStackTrace();
            return false;
        }
    }
}
