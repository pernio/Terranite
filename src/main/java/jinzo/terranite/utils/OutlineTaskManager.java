package jinzo.terranite.utils;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import jinzo.terranite.Terranite;
import jinzo.terranite.events.OutlineTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutlineTaskManager {
    private static final Map<UUID, ScheduledTask> runningTasks = new HashMap<>();

    public static void start(Player player) {
        UUID uuid = player.getUniqueId();
        if (runningTasks.containsKey(uuid)) return;

        RegionScheduler scheduler = Terranite.getInstance().getServer().getRegionScheduler();
        Location loc = player.getLocation();

        ScheduledTask task = scheduler.runAtFixedRate(
                Terranite.getInstance(),
                loc,
                (scheduledTask) -> new OutlineTask(player).run(),
                1L,
                20L
        );

        runningTasks.put(uuid, task);
    }

    public static void stop(Player player) {
        UUID uuid = player.getUniqueId();
        ScheduledTask task = runningTasks.remove(uuid);
        if (task != null) task.cancel();
    }

    public static void stopAll() {
        for (ScheduledTask task : runningTasks.values()) {
            task.cancel();
        }
        runningTasks.clear();
    }
}
