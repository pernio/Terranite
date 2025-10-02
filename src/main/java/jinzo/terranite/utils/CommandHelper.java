package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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

    // Overload
    public static void sendSuccess(CommandSender sender, Component message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            sender.sendMessage(prefix.append(message));
        }
    }

    public static void sendError(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.RED);
            sender.sendMessage(prefix.append(body));
        }
    }

    // Overload
    public static void sendError(CommandSender sender, Component message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            sender.sendMessage(prefix.append(message));
        }
    }

    public static void sendInfo(CommandSender sender, String message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            Component body = Component.text(message, NamedTextColor.YELLOW);
            sender.sendMessage(prefix.append(body));
        }
    }

    // Overload
    public static void sendInfo(CommandSender sender, Component message) {
        if (sender != null && message != null) {
            Component prefix = Component.text("[Terra] ", NamedTextColor.GOLD);
            sender.sendMessage(prefix.append(message));
        }
    }

    public static boolean isTerraWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false; // redundant now, but safe

        NamespacedKey key = new NamespacedKey(Terranite.getInstance(), "terra_wand");
        return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    public static boolean checkMultipleWands(Player player) {
        if (!config.allowMultipleWands) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (isTerraWand(item)) {
                    sendError(player, "You already have a Terra wand!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Modifies blocks in player's selection matching filter to the given material.
     * Records the original block states for undo functionality.
     * This must be called from the main thread because it calls Bukkit API methods.
     *
     * @return number of blocks changed, -1 if positions are not set, -2 if selection is too large
     */
    public static int modifySelection(Player player, Material material, Predicate<Block> filter, @Nullable Location pos1, @Nullable Location pos2) {
        var selection = SelectionManager.getSelection(player);
        if ((pos1 == null && selection.pos1 == null) || (pos2 == null && selection.pos2 == null)) return -1;
        boolean inverted = config.excludeNotifiedBlocks;

        Location loc1 = pos1 == null ? selection.pos1 : pos1;
        Location loc2 = pos2 == null ? selection.pos2 : pos2;

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

        // Get player's facing direction once
        BlockFace playerFacing = getPlayerFacing(player);
        BlockFace oppositeFacing = getOppositeFace(playerFacing);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (filter.test(block)) {
                        // Log destroying block
                        snapshot.put(block.getLocation(), block.getType());
                        Material oldMaterial = block.getType();
                        if (!oldMaterial.equals(material)) CoreProtectHook.logDestroy(player, block.getLocation(), oldMaterial);

                        // Set the block type first
                        block.setType(material);
                        if (!oldMaterial.equals(material)) CoreProtectHook.logCreate(player, block.getLocation(), material);

                        // If the block is directional, set its facing
                        if (block.getBlockData() instanceof Directional) {
                            Directional directional = (Directional) block.getBlockData();

                            // Special case for stairs - face same direction as player
                            if (block.getBlockData() instanceof Stairs) {
                                directional.setFacing(playerFacing);
                            } else {
                                // All other directional blocks face opposite direction
                                directional.setFacing(oppositeFacing);
                            }

                            block.setBlockData(directional);
                        }

                        changed++;

                        // Log notification if it's a notified block
                        if (inverted != config.notifiedMaterials.contains(material)) {
                            notifiedCount.merge(material, 1, Integer::sum);
                            firstLocation.putIfAbsent(material, block.getLocation());
                        }
                    }
                }
            }
        }

        if (!snapshot.isEmpty()) {
            ActionHistoryManager.record(player, snapshot);
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

        return changed;
    }

    private static BlockFace getPlayerFacing(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw < 225) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }

    private static BlockFace getOppositeFace(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.NORTH;
            case EAST -> BlockFace.WEST;
            case WEST -> BlockFace.EAST;
            default -> face; // return same face if not cardinal direction
        };
    }

    public static int modifySelection(Player player, BlockModifier modifier, @Nullable Location pos1, @Nullable Location pos2) {
        var selection = SelectionManager.getSelection(player);
        if ((pos1 == null && selection.pos1 == null) || (pos2 == null && selection.pos2 == null)) return -1;
        boolean inverted = config.excludeNotifiedBlocks;

        Location loc1 = pos1 == null ? selection.pos1 : pos1;
        Location loc2 = pos2 == null ? selection.pos2 : pos2;

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

                        if (inverted != config.notifiedMaterials.contains(block.getType())) {
                            Material after = block.getType();
                            notifiedCount.merge(after, 1, Integer::sum);
                            firstLocation.putIfAbsent(after, block.getLocation());
                        }

                        // Coreprotect logs
                        if (!before.equals(block.getType())) CoreProtectHook.logDestroy(player, block.getLocation(), before);
                        if (!before.equals(block.getType())) CoreProtectHook.logCreate(player, block.getLocation(), block.getType());
                    }
                }
            }
        }

        if (!snapshot.isEmpty()) {
            ActionHistoryManager.record(player, snapshot);
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
        int maxSelectionSize = config.maxSelectionSize;
        if (maxSelectionSize != -1 && !player.hasPermission("terranite.exempt.selection") && volume > maxSelectionSize) {
            sendError(player, "Selection too large. Limit is " + maxSelectionSize + (maxSelectionSize == 1 ? " block." : " blocks."));
            return false;
        }
        return true;
    }

    public static boolean checkMaterialBlocked(Player player, Material material) {
        if (material == null || !material.isBlock()) return false;
        boolean inverted = config.excludeBlockedBlocks;
        if (player.hasPermission("terranite.exempt.blockedBlocks"))
            return false;

        if (inverted != config.blockedMaterials.contains(material)) {
            sendError(player, "This block is forbidden to use.");
            return true;
        }
        return false;
    }

    public static void checkClearSelection(Player player) {
        // Clear selection after command if enabled in config
        if (config.clearSelectionAfterCommand) {
            SelectionManager.clearSelection(player);
        }
    }

    public static void playSound(Player player, Location location) {
        if (!config.playSound) return;

        // Play effect if enabled in config
        player.playSound(location, config.selectSound, 1.0f, 1.6f);
    }

    public static void logMessage(Player player, String message) {
        // Always log to console
        Bukkit.getLogger().info(message);

        if (!config.logNotifications) return;

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

    public static int previewSelection(Player player, Material material, Predicate<Block> filter) {
        SelectionManager.Selection sel = SelectionManager.getSelection(player);
        if (!sel.isComplete()) return -1;

        Map<Location, Material> previewMap = new HashMap<>();
        for (Location loc : sel.getLocations()) {
            if (filter.test(loc.getBlock())) {
                previewMap.put(loc, material);
            }
        }

        if (previewMap.isEmpty()) return -2;

        PreviewManager.preview(player, previewMap);
        return previewMap.size();
    }

    public static void requestPreview(Player player, @Nullable Material material, int changed) {

        Component message = Component.text("[Terra] ").color(NamedTextColor.GOLD)
                .append(Component.text("Previewed " + changed + " blocks" + (material == null ? "" : " " + material.name().toLowerCase()) + ".\n").color(NamedTextColor.DARK_GREEN))
                .append(Component.text("[ //apply ]")
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("//apply"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to apply the previewed blocks"))))
                .append(Component.space())
                .append(Component.text("[ //cancel ]")
                        .color(NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("//cancel"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to cancel the preview"))));

        player.sendMessage(message);
    }
}
