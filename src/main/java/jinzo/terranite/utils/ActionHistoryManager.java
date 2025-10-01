package jinzo.terranite.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ActionHistoryManager {
    private static final Map<UUID, ConcurrentLinkedDeque<Map<Location, Material>>> undoStack = new ConcurrentHashMap<>();
    private static final Map<UUID, ConcurrentLinkedDeque<Map<Location, Material>>> redoStack = new ConcurrentHashMap<>();

    public static void record(Player player, Map<Location, Material> snapshot) {
        UUID uuid = player.getUniqueId();
        undoStack.computeIfAbsent(uuid, k -> new ConcurrentLinkedDeque<>()).push(snapshot);
        redoStack.remove(uuid);
    }

    public static boolean undo(Player player) {
        UUID uuid = player.getUniqueId();
        var playerUndoStack = undoStack.get(uuid);
        if (playerUndoStack == null || playerUndoStack.isEmpty()) return false;

        Map<Location, Material> snapshot = playerUndoStack.pop();
        Map<Location, Material> redoSnapshot = new ConcurrentHashMap<>();

        for (Map.Entry<Location, Material> entry : snapshot.entrySet()) {
            // Log destroying block
            Block block = player.getWorld().getBlockAt(entry.getKey());
            Material oldType = block.getType();
            CoreProtectHook.logDestroy(player, block.getLocation(), oldType);

            // Log after modification
            redoSnapshot.put(entry.getKey(), oldType);
            block.setType(entry.getValue());
            CoreProtectHook.logCreate(player, block.getLocation(), block.getType());
        }

        redoStack.computeIfAbsent(uuid, k -> new ConcurrentLinkedDeque<>()).push(redoSnapshot);
        return true;
    }

    public static boolean redo(Player player) {
        UUID uuid = player.getUniqueId();
        var playerRedoStack = redoStack.get(uuid);
        if (playerRedoStack == null || playerRedoStack.isEmpty()) return false;

        Map<Location, Material> snapshot = playerRedoStack.pop();
        Map<Location, Material> undoSnapshot = new ConcurrentHashMap<>();

        for (Map.Entry<Location, Material> entry : snapshot.entrySet()) {
            // Log destroying block
            Block block = player.getWorld().getBlockAt(entry.getKey());
            Material oldType = block.getType();
            CoreProtectHook.logDestroy(player, block.getLocation(), oldType);

            // Log after modification
            undoSnapshot.put(entry.getKey(), oldType);
            block.setType(entry.getValue());
            CoreProtectHook.logCreate(player, block.getLocation(), block.getType());
        }

        undoStack.computeIfAbsent(uuid, k -> new ConcurrentLinkedDeque<>()).push(undoSnapshot);
        return true;
    }
}
