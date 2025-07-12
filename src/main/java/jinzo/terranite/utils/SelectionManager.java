package jinzo.terranite.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    }

    public static void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
    }
}
