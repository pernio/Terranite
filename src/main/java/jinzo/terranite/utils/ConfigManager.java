package jinzo.terranite.utils;

import jinzo.terranite.Terranite;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.configuration.file.FileConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

public class ConfigManager {
    private final Terranite plugin;

    public boolean lockdown = false;
    public int maxSelectionSize;
    public boolean excludeBlockedBlocks = false;
    public boolean excludeNotifiedBlocks = false;
    public List<String> blockedBlocks;
    public List<String> notifiedBlocks;
    public final Set<Material> blockedMaterials = new HashSet<>();
    public final Set<Material> notifiedMaterials = new HashSet<>();

    public String selectEffectColor = "FUCHSIA";
    public DustOptions selectEffectDust;

    public String outlineEffectColor = "FUCHSIA";
    public DustOptions outlineEffectDust;
    public int outlineEffectSpeed = 4;

    public String selectSoundName = "BLOCK_NOTE_BLOCK_PLING";
    public Sound selectSound = Sound.BLOCK_NOTE_BLOCK_PLING;

    public String wandMaterialName = "FEATHER";
    public Material wandMaterial = Material.FEATHER;

    public String wandColorName = "gold";
    public NamedTextColor wandColor = NamedTextColor.GOLD;

    public String wandName = "Terra Wand";
    public String wandDescription = "This is a tool used for Terranite.";
    public boolean playSound = false;
    public boolean logNotifications = false;
    public boolean hideSelectionWhenHoldingOtherItem = true;
    public boolean clearSelectionAfterCommand = true;
    public long commandCooldown = 0;
    public boolean safeDeleteSchematic = true;
    public String coreprotectUser = "#terranite";

    public boolean deleteWandOnDrop = true;
    public boolean deleteWandOnStore = true;
    public boolean deleteWandOnPickup = true;
    public boolean deleteWandOnShot = true;
    public boolean allowMultipleWands = false;

    public ConfigManager(Terranite plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        lockdown = cfg.getBoolean("lockdown", false);
        maxSelectionSize = cfg.getInt("max_selection_size", 500_000);
        excludeBlockedBlocks = cfg.getBoolean("exclude_blocked_blocks", false);
        excludeNotifiedBlocks = cfg.getBoolean("exclude_notified_blocks", false);

        selectEffectColor = cfg.getString("select_effect_color", "FUCHSIA").toUpperCase();
        selectEffectDust = createDustOptions(selectEffectColor);

        outlineEffectColor = cfg.getString("outline_effect_color", "FUCHSIA").toUpperCase();
        outlineEffectDust = createDustOptions(outlineEffectColor);
        outlineEffectSpeed = Math.max(1, Math.min(cfg.getInt("outline_effect_speed", 4), 4));

        wandName = cfg.getString("wand_name", "Terra Wand");
        wandDescription = cfg.getString("wand_description", "This is a tool used for Terranite.");
        selectSoundName = cfg.getString("select_sound", "BLOCK_NOTE_BLOCK_PLING").toUpperCase();
        playSound = cfg.getBoolean("play_sound", false);
        logNotifications = cfg.getBoolean("log_notifications", false);
        hideSelectionWhenHoldingOtherItem = cfg.getBoolean("hide_selection_when_holding_other_item", true);
        clearSelectionAfterCommand = cfg.getBoolean("clear_selection_after_command", true);
        commandCooldown = cfg.getLong("command_cooldown", 0) * 1000L;
        safeDeleteSchematic = cfg.getBoolean("safe_delete_schematic", true);
        coreprotectUser = cfg.getString("coreprotect_user", "#terranite");

        deleteWandOnDrop = cfg.getBoolean("delete_wand_on_drop", true);
        deleteWandOnStore = cfg.getBoolean("delete_wand_on_store", true);
        deleteWandOnPickup = cfg.getBoolean("delete_wand_on_pickup", true);
        deleteWandOnShot = cfg.getBoolean("delete_wand_on_shot", true);
        allowMultipleWands = cfg.getBoolean("allow_multiple_wands", false);

        wandMaterialName = cfg.getString("wand_material", "FEATHER").toUpperCase();
        wandMaterial = Material.valueOf(wandMaterialName.equals("ARROW") ? "ARROW" : "FEATHER");

        wandColorName = cfg.getString("wand_color", "gold").toLowerCase();
        try {
            wandColor = NamedTextColor.NAMES.value(wandColorName);
        } catch (IllegalArgumentException | NullPointerException e) {
            plugin.getLogger().warning("Invalid wand_color: " + wandColorName + ", defaulting to GOLD");
            wandColor = NamedTextColor.GOLD;
        }

        try {
            selectSound = Sound.valueOf(selectSoundName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound: " + selectSoundName + ", defaulting to BLOCK_NOTE_BLOCK_PLING");
            selectSound = Sound.BLOCK_NOTE_BLOCK_PLING;
        }

        blockedBlocks = cfg.getStringList("blocked_blocks");
        notifiedBlocks = cfg.getStringList("notified_blocks");

        blockedMaterials.clear();
        for (String name : blockedBlocks) {
            Material mat = Material.matchMaterial(name);
            if (mat != null) blockedMaterials.add(mat);
        }

        notifiedMaterials.clear();
        for (String name : notifiedBlocks) {
            Material mat = Material.matchMaterial(name);
            if (mat != null) notifiedMaterials.add(mat);
        }
    }

    private DustOptions createDustOptions(String colorName) {
        try {
            Color color = (Color) Color.class.getField(colorName.toUpperCase()).get(null);
            return new DustOptions(color, 1.0f);
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid color: " + colorName + ", defaulting to FUCHSIA");
            return new DustOptions(Color.FUCHSIA, 1.0f);
        }
    }
}
