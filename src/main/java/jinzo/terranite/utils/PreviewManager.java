package jinzo.terranite.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PreviewManager {
    private static final Map<UUID, Map<Location, Material>> previews = new HashMap<>();

    public static void preview(Player player, Map<Location, Material> blocks) {
        previews.put(player.getUniqueId(), blocks);
        blocks.forEach((loc, mat) -> {
            player.sendBlockChange(loc, mat.createBlockData());
        });
    }

    public static void apply(Player player) {
        Map<Location, Material> blocks = previews.remove(player.getUniqueId());
        if (blocks != null) {
            blocks.forEach((loc, mat) -> {
                loc.getBlock().setType(mat);
            });
        }
    }

    public static void cancel(Player player) {
        Map<Location, Material> blocks = previews.remove(player.getUniqueId());
        if (blocks != null) {
            blocks.keySet().forEach(loc -> {
                player.sendBlockChange(loc, loc.getBlock().getBlockData());
            });
        }
    }

    public static boolean hasPreview(Player player) {
        return previews.containsKey(player.getUniqueId());
    }
}
