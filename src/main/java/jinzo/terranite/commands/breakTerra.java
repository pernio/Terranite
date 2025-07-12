package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class breakTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        if (args.length < 2) {
            return setTerra.onCommand(sender, command, label, new String[]{"set", "air"});
        }

        Set<Material> targetMaterials = new HashSet<>();
        for (int i = 1; i < args.length; i++) {
            Material mat = Material.matchMaterial(args[i]);
            if (mat == null || !mat.isBlock()) {
                CommandHelper.sendError(player, "Invalid block type: " + args[i]);
                return false;
            }
            targetMaterials.add(mat);
        }

        int result = CommandHelper.modifySelection(player, block -> {
            if (targetMaterials.contains(block.getType())) {
                block.setType(Material.AIR);
                return true;
            }
            return false;
        });

        if (result == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        }

        if (result == -2) return false;

        CommandHelper.sendSuccess(player, "Deleted " + result + " block(s).");
        return true;
    }
}
