package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    public int maxSelectionSize = 500_000;


    public ConfigManager(Terranite plugin) {
        FileConfiguration cfg = plugin.getConfig();

        this.maxSelectionSize = cfg.getInt("max_selection_size", 500_000);
    }
}
