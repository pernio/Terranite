package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {
    private final Terranite plugin;

    public int maxSelectionSize;
    public List<String> blockedBlocks;
    public List<String> notifiedBlocks;
    public final Set<Material> blockedMaterials = new HashSet<>();
    public final Set<Material> notifiedMaterials = new HashSet<>();
    public String selectEffectName = "HAPPY_VILLAGER";
    public Particle selectEffect = Particle.HAPPY_VILLAGER;
    public String outlineEffectName = "HAPPY_VILLAGER";
    public Particle outlineEffect = Particle.HAPPY_VILLAGER;
    public String selectSoundName = "BLOCK_NOTE_BLOCK_PLING";
    public Sound selectSound = Sound.BLOCK_NOTE_BLOCK_PLING;
    public boolean playSound = false;
    public boolean logNotifications = false;

    public ConfigManager(Terranite plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        maxSelectionSize = cfg.getInt("max_selection_size", 500_000);
        selectEffectName = cfg.getString("select_effect", "HAPPY_VILLAGER").toUpperCase();
        outlineEffectName = cfg.getString("outline_effect", "HAPPY_VILLAGER").toUpperCase();
        selectSoundName = cfg.getString("select_sound", "BLOCK_NOTE_BLOCK_BASS").toUpperCase();
        blockedBlocks = cfg.getStringList("blocked_blocks");
        notifiedBlocks = cfg.getStringList("notified_blocks");
        playSound = cfg.getBoolean("play_sound", false);
        logNotifications = cfg.getBoolean("log_notifications", false);

        try {
            selectEffect = Particle.valueOf(selectEffectName);
        } catch (IllegalArgumentException e) {
            selectEffect = Particle.HAPPY_VILLAGER;
            plugin.getLogger().warning("Invalid particle in config: " + selectEffectName + ", defaulting to HAPPY_VILLAGER");
        }

        try {
            outlineEffect = Particle.valueOf(outlineEffectName);
        } catch (IllegalArgumentException e) {
            outlineEffect = Particle.HAPPY_VILLAGER;
            plugin.getLogger().warning("Invalid particle in config: " + outlineEffectName + ", defaulting to HAPPY_VILLAGER");
        }

        try {
            selectSound = Sound.valueOf(selectSoundName);
        } catch (IllegalArgumentException e) {
            selectSound = Sound.BLOCK_NOTE_BLOCK_PLING;
            plugin.getLogger().warning("Invalid sound in config: " + selectSoundName + ", defaulting to BLOCK_NOTE_BLOCK_PLING");
        }

        blockedMaterials.clear();
        for (String blockName : blockedBlocks) {
            Material mat = Material.matchMaterial(blockName);
            if (mat != null) {
                blockedMaterials.add(mat);
            }
        }

        notifiedMaterials.clear();
        for (String blockName : notifiedBlocks) {
            Material mat = Material.matchMaterial(blockName);
            if (mat != null) {
                notifiedMaterials.add(mat);
            }
        }
    }
}
