package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {
    public int maxSelectionSize = 500_000;
    public List<String> blockedBlocks;
    public final Set<Material> blockedMaterials = new HashSet<>();

    public ConfigManager(Terranite plugin) {
        FileConfiguration cfg = plugin.getConfig();

        this.maxSelectionSize = cfg.getInt("max_selection_size", 500_000);
        this.blockedBlocks = cfg.getStringList("blocked_blocks");

        for (String blockName : blockedBlocks) {
            Material mat = Material.matchMaterial(blockName);
            if (mat != null) {
                blockedMaterials.add(mat);
            }
        }
    }
}
