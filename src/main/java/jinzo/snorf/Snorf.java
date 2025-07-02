package jinzo.snorf;

import jinzo.snorf.commands.snorfCommand;
import jinzo.snorf.listeners.JoinListener;
import jinzo.snorf.listeners.SelectionListener;
import jinzo.snorf.utils.ConfigManager;
import jinzo.snorf.utils.OutlineTaskManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Snorf extends JavaPlugin {
    private static Snorf instance;
    private ConfigManager configuration;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configuration = new ConfigManager(this);

        event(new SelectionListener(), this);
        event(new JoinListener(), this);
        snorfCommand snorfCommand = new snorfCommand();
        getCommand("s").setExecutor(snorfCommand);
        getCommand("s").setTabCompleter(snorfCommand);
        getLogger().info("Snorf has been enabled!");

    }

    @Override
    public void onDisable() {
        OutlineTaskManager.stopAll();
        getLogger().info("Snorf has been disabled!");
        instance = null;
    }

    public void event(Listener listener, Plugin plugin) {
        getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static Snorf getInstance() {
        return instance;
    }

    public ConfigManager getConfiguration() { return this.configuration; }
}
