package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class wandTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        try {
            ItemStack wand = new ItemStack(Material.ARROW);
            ItemMeta meta = wand.getItemMeta();
            if (meta != null) {
                meta.displayName(
                        Component.text("Terra wand", NamedTextColor.GOLD)
                                .decoration(TextDecoration.ITALIC, false)
                );
                meta.lore(List.of(Component.text("This is a tool used for Terranite", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
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
