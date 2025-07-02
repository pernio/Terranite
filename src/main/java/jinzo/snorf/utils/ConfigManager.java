package jinzo.snorf.utils;

import jinzo.snorf.Snorf;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    public int maxSelectionSize = 500_000;


    public ConfigManager(Snorf plugin) {
        FileConfiguration cfg = plugin.getConfig();

        this.maxSelectionSize = cfg.getInt("max_selection_size", 500_000);
    }
}
