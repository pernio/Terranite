package jinzo.terranite.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SelectionManager {

    private static final Map<UUID, Selection> selections = new ConcurrentHashMap<>();

    public static void setPos1(Player player, Location location) {
        getSelection(player.getUniqueId()).pos1 = location.clone();
    }

    public static void setPos2(Player player, Location location) {
        getSelection(player.getUniqueId()).pos2 = location.clone();
    }

    public static Selection getSelection(Player player) {
        return getSelection(player.getUniqueId());
    }

    private static Selection getSelection(UUID playerUUID) {
        return selections.computeIfAbsent(playerUUID, id -> new Selection());
    }

    public static class Selection {
        public Location pos1 = null;
        public Location pos2 = null;

        public boolean isComplete() {
            return pos1 != null && pos2 != null;
        }

        /**
         * Returns all locations in the cuboid defined by pos1 and pos2.
         */
        public List<Location> getLocations() {
            List<Location> locations = new ArrayList<>();
            if (!isComplete()) return locations;

            World world = pos1.getWorld();
            if (world == null) return locations;

            int xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
            int yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
            int zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

            int xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
            int yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
            int zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        locations.add(new Location(world, x, y, z));
                    }
                }
            }

            return locations;
        }
    }

    public static void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
    }
}
