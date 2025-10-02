package jinzo.terranite.commands.schematic;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SchematicIO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class listTerra implements CommandExecutor {

    private final SchematicIO schematicIO;

    public listTerra(SchematicIO schematicIO) {
        this.schematicIO = schematicIO;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this command.");
            return false;
        }

        File schematicsFolder = schematicIO.getSchematicsFolder();
        File[] files = schematicsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));

        if (files == null || files.length == 0) {
            CommandHelper.sendError(player, "No schematics found.");
            return true;
        }

        List<String> owned = new ArrayList<>();
        List<String> others = new ArrayList<>();

        boolean isAdmin = player.hasPermission("terranite.admin");

        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String creatorStr = config.getString("creator");

            if (creatorStr == null) {
                others.add(name + " " + ChatColor.DARK_GRAY + "(corrupted)");
                continue;
            }

            try {
                UUID uuid = UUID.fromString(creatorStr);
                if (uuid.equals(player.getUniqueId())) {
                    owned.add(name);
                } else if (isAdmin) {
                    others.add(name + ChatColor.DARK_GRAY + " (not yours)");
                }
            } catch (IllegalArgumentException e) {
                others.add(name + " " + ChatColor.DARK_GRAY + "(corrupted)");
            }
        }

        if (!owned.isEmpty()) {
            CommandHelper.sendSuccess(player, ChatColor.GRAY + "Your schematics:");
            player.sendMessage(String.join(ChatColor.WHITE + ", ", owned));
        }

        if (isAdmin && !others.isEmpty()) {
            player.sendMessage("");
            CommandHelper.sendInfo(player, ChatColor.GRAY + "Other schematics:");
            player.sendMessage(String.join(ChatColor.DARK_GRAY + ", ", others));
        }

        return true;
    }
}
