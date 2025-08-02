package jinzo.terranite.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static jinzo.terranite.utils.CommandHelper.isTerraWand;
import static org.bukkit.Bukkit.getLogger;

public class wandTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        ConfigManager config = Terranite.getInstance().getConfiguration();
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (!Terranite.getInstance().getConfiguration().allowMultipleWands) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (isTerraWand(item)) {
                    CommandHelper.sendError(player, "You already have a Terra wand!");
                    return false;
                }
            }
        }

        try {
            ItemStack wand = new ItemStack(config.wandMaterial);
            ItemMeta meta = wand.getItemMeta();
            if (meta != null) {
                // Set display name and lore (optional)
                meta.displayName(
                        Component.text(config.wandName, config.wandColor)
                                .decoration(TextDecoration.ITALIC, false)
                );
                if (config.wandDescription != null && !config.wandDescription.isEmpty()) {
                    meta.lore(List.of(Component.text(config.wandDescription, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
                }

                // Set PersistentDataContainer tag
                NamespacedKey key = new NamespacedKey(Terranite.getInstance(), "terra_wand");
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

                wand.setItemMeta(meta);
            }

            player.getInventory().addItem(wand);
            CommandHelper.sendSuccess(player, "You received the Terra wand!");

            return true;
        } catch (Exception e) {
            getLogger().severe("Error while creating wand: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
