package jinzo.snorf.utils;

import org.bukkit.block.data.BlockData;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClipboardManager {
    public record ClipboardData(Map<String, BlockData> blocks, int width, int height, int depth, Location origin, float yaw, float pitch) {}

    private static final Map<UUID, ClipboardData> clipboards = new HashMap<>();

    public static void setClipboard(UUID playerId, Map<String, BlockData> blocks, int w, int h, int d, Location origin, float yaw, float pitch) {
        clipboards.put(playerId, new ClipboardData(blocks, w, h, d, origin, yaw, pitch));
    }

    public static ClipboardData getClipboard(UUID playerId) {
        return clipboards.get(playerId);
    }

    public static boolean hasClipboard(UUID playerId) {
        return clipboards.containsKey(playerId);
    }
}
