package jinzo.snorf.utils;

import jinzo.snorf.events.OutlineTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class OutlineTaskManager {
    private static final HashMap<UUID, BukkitTask> runningTasks = new HashMap<>();

    public static void start(Player player) {
        UUID uuid = player.getUniqueId();

        if (runningTasks.containsKey(uuid)) return;

        BukkitTask task = new OutlineTask(player).runTaskTimerAsynchronously(
                jinzo.snorf.Snorf.getInstance(), 0L, 20L);
        runningTasks.put(uuid, task);
    }

    public static void stop(Player player) {
        UUID uuid = player.getUniqueId();

        BukkitTask task = runningTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    public static void stopAll() {
        for (BukkitTask task : runningTasks.values()) {
            task.cancel();
        }
        runningTasks.clear();
    }
}
