package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public final class CoreProtectHook {
    static ConfigManager config = Terranite.getInstance().getConfiguration();

    private CoreProtectHook() {}

    public static void logCreate(Player player, Location loc, Material type) {
        var plugin = Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (plugin == null) return;
        if (type.equals(Material.AIR)) return;

        try {
            Class<?> cpClass = plugin.getClass();
            var method = cpClass.getMethod("getAPI");
            Object api = method.invoke(plugin);

            if (api != null) {
                api.getClass()
                        .getMethod("logPlacement", String.class, Location.class, Material.class, BlockData.class)
                        .invoke(api, player.getName(), loc, type, null);
            }
        } catch (Exception ignored) {}
    }

    public static void logDestroy(Player player, Location loc, Material type) {
        var plugin = Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (plugin == null) return;
        if (type.equals(Material.AIR)) return;

        try {
            Class<?> cpClass = plugin.getClass();
            var method = cpClass.getMethod("getAPI");
            Object api = method.invoke(plugin);

            if (api != null) {
                api.getClass()
                        .getMethod("logRemoval", String.class, Location.class, Material.class, BlockData.class)
                        .invoke(api, player.getName(), loc, type, null);
            }
        } catch (Exception ignored) {}
    }
}
