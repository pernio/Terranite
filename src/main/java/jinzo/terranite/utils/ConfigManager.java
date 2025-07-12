package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {
    private final Terranite plugin;

    public int maxSelectionSize;
    public List<String> blockedBlocks;
    public final Set<Material> blockedMaterials = new HashSet<>();

    public ConfigManager(Terranite plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        maxSelectionSize = cfg.getInt("max_selection_size", 500_000);
        blockedBlocks = cfg.getStringList("blocked_blocks");

        blockedMaterials.clear();
        for (String blockName : blockedBlocks) {
            Material mat = Material.matchMaterial(blockName);
            if (mat != null) {
                blockedMaterials.add(mat);
            }
        }
    }
}
