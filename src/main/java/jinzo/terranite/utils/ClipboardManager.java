package jinzo.terranite.utils;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClipboardManager {

    public record ClipboardData(
            Map<String, BlockData> blocks,
            int width,
            int height,
            int depth,
            Location origin,
            float yaw,
            float pitch
    ) {}

    private static final Map<UUID, ClipboardData> clipboards = new ConcurrentHashMap<>();

    public static void setClipboard(UUID playerId,
                                    Map<String, BlockData> blocks,
                                    int width,
                                    int height,
                                    int depth,
                                    Location origin,
                                    float yaw,
                                    float pitch) {

        clipboards.put(playerId, new ClipboardData(blocks, width, height, depth, origin, yaw, pitch));
    }

    public static ClipboardData getClipboard(UUID playerId) {
        return clipboards.get(playerId);
    }

    public static boolean hasClipboard(UUID playerId) {
        return clipboards.containsKey(playerId);
    }
}
