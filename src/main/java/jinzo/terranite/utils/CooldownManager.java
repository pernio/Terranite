package jinzo.terranite.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID uuid, long durationMillis) {
        cooldowns.put(uuid, System.currentTimeMillis() + durationMillis);
    }

    public boolean isOnCooldown(UUID uuid) {
        return cooldowns.containsKey(uuid) && System.currentTimeMillis() < cooldowns.get(uuid);
    }

    public long getRemaining(UUID uuid) {
        return Math.max(0, cooldowns.getOrDefault(uuid, 0L) - System.currentTimeMillis());
    }
}
