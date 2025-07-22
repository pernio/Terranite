package jinzo.terranite.events;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.ConfigManager;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OutlineTask {
    private final Player player;

    public OutlineTask(Player player) {
        this.player = player;
    }

    public void run() {
        SelectionManager.Selection sel = SelectionManager.getSelection(player);
        if (sel.pos1 == null || sel.pos2 == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!CommandHelper.isTerraWand(item)) return;

        Location min = sel.pos1;
        Location max = sel.pos2;

        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int edges = 0;
                    if (x == minX || x == maxX) edges++;
                    if (y == minY || y == maxY) edges++;
                    if (z == minZ || z == maxZ) edges++;

                    if (edges >= 2) {
                        Location loc = new Location(player.getWorld(), x + 0.5, y + 0.5, z + 0.5);
                        player.spawnParticle(Terranite.getInstance().getConfiguration().outlineEffect, loc, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}
