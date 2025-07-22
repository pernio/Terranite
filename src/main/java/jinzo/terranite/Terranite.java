package jinzo.terranite;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import jinzo.terranite.commands.pasteTerra;
import jinzo.terranite.commands.saveTerra;
import jinzo.terranite.commands.terraCommand;
import jinzo.terranite.listeners.JoinListener;
import jinzo.terranite.listeners.PickupListener;
import jinzo.terranite.listeners.SelectionListener;
import jinzo.terranite.utils.ConfigManager;
import jinzo.terranite.utils.OutlineTaskManager;
import jinzo.terranite.utils.SchematicIO;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public final class Terranite extends JavaPlugin {
    private static Terranite instance;
    private ConfigManager configuration;
    private SchematicIO schematicIO;

    private static final String MODRINTH_PROJECT_ID = "terranite";
    private static String CURRENT_VERSION = null;

    @Override
    public void onEnable() {
        instance = this;

        checkForUpdates();
        saveDefaultConfig();
        configuration = new ConfigManager(this);

        event(new SelectionListener(), this);
        event(new JoinListener(), this);
        event(new PickupListener(this), this);
        schematicIO = new SchematicIO(instance);
        pasteTerra pasteCommand = new pasteTerra(schematicIO);
        saveTerra saveCommand = new saveTerra(schematicIO);
        terraCommand terraCommand = new terraCommand(pasteCommand, saveCommand);

        getCommand("s").setExecutor(terraCommand);
        getCommand("s").setTabCompleter(terraCommand);
        getLogger().info("Terranite has been enabled!");
        CURRENT_VERSION = Terranite.getInstance().getDescription().getVersion();
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

    private void checkForUpdates() {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.modrinth.com/v2/project/" + MODRINTH_PROJECT_ID + "/version");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                try (InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                     Scanner scanner = new Scanner(reader)) {
                    StringBuilder jsonText = new StringBuilder();
                    while (scanner.hasNext()) {
                        jsonText.append(scanner.nextLine());
                    }

                    JsonArray versions = JsonParser.parseString(jsonText.toString()).getAsJsonArray();
                    if (versions.size() == 0) {
                        getLogger().info("Could not find any versions on Modrinth.");
                        return;
                    }

                    JsonObject latestVersion = versions.get(0).getAsJsonObject();
                    String latestVersionName = latestVersion.get("version_number").getAsString();

                    if (!CURRENT_VERSION.equalsIgnoreCase(latestVersionName)) {
                        String downloadURL = "https://modrinth.com/plugin/" + MODRINTH_PROJECT_ID + "/version/" + latestVersion.get("id").getAsString();

                        String reset = "\u001B[0m";
                        String grey = "\u001B[37m";
                        String red = "\u001B[31m";
                        String green = "\u001B[32m";
                        String yellow = "\u001B[33m";

                        getLogger().info("\n\n" +
                                grey + "------------------------------------------" + reset + "\n" +
                                grey + "A new version of " + yellow + "Terranite" + grey + " is available!" + reset + "\n" +
                                grey + "New Version: " + red + CURRENT_VERSION + reset +
                                grey + " -> " +
                                green + latestVersionName + reset + "\n" +
                                "Download here: " + downloadURL + "\n" +
                                grey + "------------------------------------------" + reset + "\n"
                        );
                    }
                }

            } catch (Exception e) {
                getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        }, "Terranite-UpdateChecker").start(); // ← background thread
    }
}
