package jinzo.terranite.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SchematicIO {

    private final Plugin plugin;

    public SchematicIO(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean saveSchematic(String name, Map<String, BlockData> blocks,
                                 Location origin) {
        File folder = getSchematicsFolder();
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, name + ".yml");
        if (file.exists()) return false;

        YamlConfiguration config = new YamlConfiguration();
        config.set("origin.world", origin.getWorld().getName());
        config.set("origin.x", origin.getX());
        config.set("origin.y", origin.getY());
        config.set("origin.z", origin.getZ());

        for (Map.Entry<String, BlockData> entry : blocks.entrySet()) {
            config.set("blocks." + entry.getKey(), entry.getValue().getAsString());
        }

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public SchematicData loadSchematic(String name) {
        File file = new File(getSchematicsFolder(), name + ".yml");
        if (!file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String worldName = config.getString("origin.world");
        double ox = config.getDouble("origin.x");
        double oy = config.getDouble("origin.y");
        double oz = config.getDouble("origin.z");
        Location origin = new Location(Bukkit.getWorld(worldName), ox, oy, oz);

        Map<String, BlockData> blocks = new HashMap<>();
        if (config.isConfigurationSection("blocks")) {
            for (String key : config.getConfigurationSection("blocks").getKeys(false)) {
                BlockData data = Bukkit.createBlockData(config.getString("blocks." + key));
                blocks.put(key, data);
            }
        }

        return new SchematicData(blocks, origin);
    }

    public static class SchematicData {
        public final Map<String, BlockData> blocks;
        public final Location origin;

        public SchematicData(Map<String, BlockData> blocks, Location origin) {
            this.blocks = blocks;
            this.origin = origin;
        }
    }

    public File getSchematicsFolder() {
        return new File(plugin.getDataFolder(), "schematics");
    }

    public List<String> getSavedSchematicNames() {
        List<String> names = new ArrayList<>();
        File folder = getSchematicsFolder();
        if (!folder.exists() || !folder.isDirectory()) return names;

        File[] files = folder.listFiles((dir, filename) -> filename.endsWith(".yml"));
        if (files == null) return names;

        for (File file : files) {
            String name = file.getName();
            int dot = name.lastIndexOf('.');
            if (dot > 0) name = name.substring(0, dot);
            names.add(name);
        }
        return names;
    }
}
