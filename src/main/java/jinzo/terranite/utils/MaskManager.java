package jinzo.terranite.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaskManager {
    private static final Map<Player, Material> playerMasks = new ConcurrentHashMap<>();

    public static void setMask(Player player, Material material) {
        playerMasks.put(player, material);
    }

    public static Material getMask(Player player) {
        return playerMasks.getOrDefault(player, Material.AIR);
    }

    public static void clearMask(Player player) {
        playerMasks.remove(player);
    }
}
