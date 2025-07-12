package jinzo.terranite.utils;

import org.bukkit.Bukkit;
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

    /**
     * Records a snapshot of blocks changed by the player.
     * Clears the redo stack.
     */
    public static void record(Player player, Map<Location, Material> snapshot) {
        UUID uuid = player.getUniqueId();
        undoStack.computeIfAbsent(uuid, k -> new ConcurrentLinkedDeque<>()).push(snapshot);
        redoStack.remove(uuid);
    }

    /**
     * Undo the last action for the player.
     * Must be called from main thread or scheduled sync task.
     * @return true if undo successful, false if no undo available
     */
    public static boolean undo(Player player) {
        UUID uuid = player.getUniqueId();
        var playerUndoStack = undoStack.get(uuid);
        if (playerUndoStack == null || playerUndoStack.isEmpty()) return false;

        Map<Location, Material> snapshot = playerUndoStack.pop();
        Map<Location, Material> redoSnapshot = new ConcurrentHashMap<>();

        for (Map.Entry<Location, Material> entry : snapshot.entrySet()) {
            Block block = player.getWorld().getBlockAt(entry.getKey());
            redoSnapshot.put(entry.getKey(), block.getType());
            block.setType(entry.getValue());
        }

        redoStack.computeIfAbsent(uuid, k -> new ConcurrentLinkedDeque<>()).push(redoSnapshot);
        return true;
    }

    /**
     * Redo the last undone action for the player.
     * Must be called from main thread or scheduled sync task.
     * @return true if redo successful, false if no redo available
     */
    public static boolean redo(Player player) {
        UUID uuid = player.getUniqueId();
        var playerRedoStack = redoStack.get(uuid);
        if (playerRedoStack == null || playerRedoStack.isEmpty()) return false;

        Map<Location, Material> snapshot = playerRedoStack.pop();
        Map<Location, Material> undoSnapshot = new ConcurrentHashMap<>();

        for (Map.Entry<Location, Material> entry : snapshot.entrySet()) {
            Block block = player.getWorld().getBlockAt(entry.getKey());
            undoSnapshot.put(entry.getKey(), block.getType());
            block.setType(entry.getValue());
        }

        undoStack.computeIfAbsent(uuid, k -> new ConcurrentLinkedDeque<>()).push(undoSnapshot);
        return true;
    }
}
