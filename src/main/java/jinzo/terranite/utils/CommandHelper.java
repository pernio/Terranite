package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class CommandHelper {
    public static void sendSuccess(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.DARK_GREEN);
            sender.sendMessage(prefix.append(body));
        }
    }

    public static void sendError(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.RED);
            sender.sendMessage(prefix.append(body));
        }
    }

    public static void sendInfo(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.YELLOW);
            sender.sendMessage(prefix.append(body));
        }
    }

    public static boolean isTerraWand(ItemStack item) {
        if (item == null || item.getType() != Material.ARROW) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta.displayName() == null) return false;

        Component expectedName = Component.text("Terra wand", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
        return meta.displayName().equals(expectedName);
    }

    /**
     * Modifies blocks in player's selection matching filter to the given material.
     * Records the original block states for undo functionality.
     * This must be called from the main thread because it calls Bukkit API methods.
     *
     * @return number of blocks changed, -1 if positions are not set, -2 if selection is too large
     */
    public static int modifySelection(Player player, Material material, Predicate<Block> filter) {

        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) return -1;

        Location loc1 = selection.pos1;
        Location loc2 = selection.pos2;

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        long totalBlocks = (long)(maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        boolean selectionValidSize = checkSelectionSize(player, totalBlocks);
        if (!selectionValidSize) return -2;

        World world = player.getWorld();
        int changed = 0;

        Map<Location, Material> snapshot = new HashMap<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (filter.test(block)) {
                        snapshot.put(block.getLocation(), block.getType());
                        block.setType(material);
                        changed++;
                    }
                }
            }
        }

        if (!snapshot.isEmpty()) {
            ActionHistoryManager.record(player, snapshot);
        }

        return changed;
    }

    public static int modifySelection(Player player, BlockModifier modifier) {
        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) return -1;

        Location loc1 = selection.pos1;
        Location loc2 = selection.pos2;

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        long totalBlocks = (long)(maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        boolean selectionValidSize = checkSelectionSize(player, totalBlocks);
        if (!selectionValidSize) return -2;

        World world = player.getWorld();
        int changed = 0;
        Map<Location, Material> snapshot = new HashMap<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material before = block.getType();
                    boolean didChange = modifier.apply(block);
                    if (didChange) {
                        snapshot.put(block.getLocation(), before);
                        changed++;
                    }
                }
            }
        }

        if (!snapshot.isEmpty()) {
            ActionHistoryManager.record(player, snapshot);
        }

        return changed;
    }

    /**
     * Counts blocks in player's selection matching filter.
     * Returns -1 if selection positions are unset, -2 if selection volume too large
     */
    public static int countInSelection(Player player, Predicate<Block> filter) {
        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) return -1;

        Location pos1 = selection.pos1;
        Location pos2 = selection.pos2;

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        long volume = (long)(maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        boolean selectionValidSize = checkSelectionSize(player, volume);
        if (!selectionValidSize) return -2;

        int count = 0;
        World world = player.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (filter.test(block)) count++;
                }
            }
        }

        return count;
    }

    public static boolean checkSelectionSize(Player player, long volume) {
        int maxSelectionSize = Terranite.getInstance().getConfiguration().maxSelectionSize;
        if (maxSelectionSize != -1 && volume > maxSelectionSize) {
            sendError(player, "Selection too large. Limit is " + maxSelectionSize + " blocks.");
            return false;
        }
        return true;
    }
}
