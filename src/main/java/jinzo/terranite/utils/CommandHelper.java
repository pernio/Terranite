package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class CommandHelper {
    static ConfigManager config = Terranite.getInstance().getConfiguration();
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
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Terranite.getInstance(), "is_wand");

        return container.has(key, PersistentDataType.BYTE) &&
                container.get(key, PersistentDataType.BYTE) == (byte) 1;
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
        boolean inverted = Terranite.getInstance().getConfiguration().excludeNotifiedBlocks;

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
        Map<Material, Integer> notifiedCount = new HashMap<>();
        Map<Material, Location> firstLocation = new HashMap<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (filter.test(block)) {
                        snapshot.put(block.getLocation(), block.getType());
                        block.setType(material);
                        changed++;

                        // Log notification if it's a notified block
                        if (inverted != Terranite.getInstance().getConfiguration().notifiedMaterials.contains(material)) {
                            notifiedCount.merge(material, 1, Integer::sum);
                            firstLocation.putIfAbsent(material, block.getLocation());
                        }
                    }
                }
            }
        }

        // Bypass logging
        if (player.hasPermission("terranite.exempt.notifiedBlocks")) return changed;

        // Log notifications for notified materials
        for (Map.Entry<Material, Integer> entry : notifiedCount.entrySet()) {
            Material mat = entry.getKey();
            int count = entry.getValue();
            Location loc = firstLocation.get(mat);

            String message = String.format(
                    "[Terra] %s placed %d blocks of %s (example at %s,%s,%s in %s)",
                    player.getName(),
                    count,
                    mat.name(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                    loc.getWorld().getName()
            );

            logMessage(player, message);
        }

        if (!snapshot.isEmpty()) {
            ActionHistoryManager.record(player, snapshot);
        }

        return changed;
    }

    public static int modifySelection(Player player, BlockModifier modifier) {
        var selection = SelectionManager.getSelection(player);
        if (selection.pos1 == null || selection.pos2 == null) return -1;
        boolean inverted = Terranite.getInstance().getConfiguration().excludeNotifiedBlocks;

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

        // For notification tracking
        Map<Material, Integer> notifiedCount = new HashMap<>();
        Map<Material, Location> firstLocation = new HashMap<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material before = block.getType();
                    boolean didChange = modifier.apply(block);
                    if (didChange) {
                        snapshot.put(block.getLocation(), before);
                        changed++;

                        if (inverted != Terranite.getInstance().getConfiguration().notifiedMaterials.contains(block.getType())) {
                            Material after = block.getType();
                            notifiedCount.merge(after, 1, Integer::sum);
                            firstLocation.putIfAbsent(after, block.getLocation());
                        }
                    }
                }
            }
        }

        // Bypass logging
        if (player.hasPermission("terranite.exempt.notifiedBlocks")) return changed;

        // Log notifications for notified materials
        for (Map.Entry<Material, Integer> entry : notifiedCount.entrySet()) {
            Material mat = entry.getKey();
            int count = entry.getValue();
            Location loc = firstLocation.get(mat);

            String message = String.format(
                    "[Terra] %s placed %d blocks of %s (example at %d,%d,%d in %s)",
                    player.getName(),
                    count,
                    mat.name(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                    loc.getWorld().getName()
            );
            logMessage(player, message);
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
        if (maxSelectionSize != -1 && !player.hasPermission("terranite.exempt.selection") && volume > maxSelectionSize) {
            sendError(player, "Selection too large. Limit is " + maxSelectionSize + (maxSelectionSize == 1 ? " block." : " blocks."));
            return false;
        }
        return true;
    }

    public static boolean checkMaterialBlocked(Player player, Material material) {
        if (material == null || !material.isBlock()) return false;
        boolean inverted = Terranite.getInstance().getConfiguration().excludeBlockedBlocks;
        if (player.hasPermission("terranite.exempt.blockedBlocks"))
            return false;

        if (inverted != Terranite.getInstance().getConfiguration().blockedMaterials.contains(material)) {
            sendError(player, "This block is forbidden to use.");
            return true;
        }
        return false;
    }

    public static void checkClearSelection(Player player) {
        // Clear selection after command if enabled in config
        if (Terranite.getInstance().getConfiguration().clearSelectionAfterCommand) {
            SelectionManager.clearSelection(player);
        }
    }

    public static void playSound(Player player, Location location) {
        if (!Terranite.getInstance().getConfiguration().playSound) return;

        // Play effect if enabled in config
        player.playSound(location, Terranite.getInstance().getConfiguration().selectSound, 1.0f, 1.6f);
    }

    public static void logMessage(Player player, String message) {
        // Always log to console
        Bukkit.getLogger().info(message);

        if (!Terranite.getInstance().getConfiguration().logNotifications) return;

        try {
            // Create logs/users directory
            File usersDir = new File(Terranite.getInstance().getDataFolder(), "logs");
            if (!usersDir.exists()) usersDir.mkdirs();

            // File per player by UUID
            File logFile = new File(usersDir, player.getUniqueId() + ".log");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            String timestamp = "[" + LocalDateTime.now().format(formatter) + "] ";

            try (FileWriter fw = new FileWriter(logFile, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                String logLine = timestamp + message.substring(7);
                bw.write(logLine);
                bw.newLine();
            }

        } catch (IOException e) {
            Bukkit.getLogger().warning("[Terra] Failed to write user log for " + player.getName() + ": " + e.getMessage());
        }
    }

    public static String formatDuration(long millis) {
        long totalSeconds = (long) Math.ceil(millis / 1000.0);

        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public static Material findMaterial(Player player, String name) {
        Material material = Material.matchMaterial(name);
        if (material == null || !material.isBlock()) {
            try {
                int legacyId = Integer.parseInt(name);
                LegacyBlockHelper.LegacyBlock legacyBlock = LegacyBlockHelper.findById(legacyId);
                if (legacyBlock == null) {
                    CommandHelper.sendError(player, "Invalid block type: " + name);
                    return null;
                }
                // Use the legacy block's name to get the Material
                material = Material.matchMaterial(legacyBlock.name);
                if (material == null) {
                    CommandHelper.sendError(player, "Legacy block not found: " + legacyBlock.name);
                    return null;
                }
            } catch (NumberFormatException e) {
                CommandHelper.sendError(player, "Invalid block type: " + name);
                return material;
            }
        }
        return material;
    }
}
