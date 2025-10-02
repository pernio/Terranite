package jinzo.terranite.commands.schematic;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.SchematicIO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class deleteTerra implements CommandExecutor {
    private final SchematicIO schematicIO;

    public deleteTerra(SchematicIO schematicIO) {
        this.schematicIO = schematicIO;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length != 2) {
            CommandHelper.sendError(player, "Usage: //schematic delete <name>");
            return false;
        }

        String name = args[1];
        File schematicFile = new File(schematicIO.getSchematicsFolder(), name + ".yml");

        if (!schematicFile.exists()) {
            CommandHelper.sendError(player, "Schematic '" + name + "' does not exist.");
            return false;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(schematicFile);
        String creatorStr = config.getString("creator");

        UUID creatorUUID = null;
        boolean corrupted = false;

        if (creatorStr == null) {
            corrupted = true;
        } else {
            try {
                creatorUUID = UUID.fromString(creatorStr);
            } catch (IllegalArgumentException e) {
                corrupted = true;
            }
        }

        if (Terranite.getInstance().getConfiguration().safeDeleteSchematic) {
            if (corrupted) {
                if (!player.hasPermission("terranite.admin")) {
                    CommandHelper.sendError(player, "Schematic metadata corrupted; only admins can delete this.");
                    return false;
                }
            } else {
                if (!player.hasPermission("terranite.admin") && !creatorUUID.equals(player.getUniqueId())) {
                    CommandHelper.sendError(player, "You are not the owner of this schematic.");
                    return false;
                }
            }
        }

        if (schematicFile.delete()) {
            CommandHelper.sendSuccess(player, "Deleted schematic '" + name + "'.");
        } else {
            CommandHelper.sendError(player, "Failed to delete schematic '" + name + "'.");
        }

        return true;
    }
}
