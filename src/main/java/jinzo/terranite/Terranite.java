package jinzo.terranite;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import jinzo.terranite.commands.pasteTerra;
import jinzo.terranite.commands.saveTerra;
import jinzo.terranite.commands.terraCommand;
import jinzo.terranite.listeners.JoinListener;
import jinzo.terranite.listeners.SelectionListener;
import jinzo.terranite.utils.ConfigManager;
import jinzo.terranite.utils.OutlineTaskManager;
import jinzo.terranite.utils.SchematicIO;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Terranite extends JavaPlugin {
    private static Terranite instance;
    private ConfigManager configuration;
    private SchematicIO schematicIO;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configuration = new ConfigManager(this);

        event(new SelectionListener(), this);
        event(new JoinListener(), this);
        schematicIO = new SchematicIO(instance);
        pasteTerra pasteCommand = new pasteTerra(schematicIO);
        saveTerra saveCommand = new saveTerra(schematicIO);
        terraCommand terraCommand = new terraCommand(pasteCommand, saveCommand);

        getCommand("s").setExecutor(terraCommand);
        getCommand("s").setTabCompleter(terraCommand);
        getLogger().info("Terranite has been enabled!");

    }

    @Override
    public void onDisable() {
        OutlineTaskManager.stopAll();
        getLogger().info("Terranite has been disabled!");
        instance = null;
    }

    public void event(Listener listener, Plugin plugin) {
        getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static Terranite getInstance() {
        return instance;
    }

    public ConfigManager getConfiguration() { return this.configuration; }

    public SchematicIO getSchematicIO() {
        return this.schematicIO;
    }
}
