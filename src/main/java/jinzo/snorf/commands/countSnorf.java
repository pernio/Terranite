package jinzo.snorf.commands;

import jinzo.snorf.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class countSnorf {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return true;
        }

        if (args.length < 2) {
            CommandHelper.sendError(player, "Usage: /s count <block1> <block2> ...");
            return true;
        }

        // Parse materials
        Map<Material, Integer> blockCounts = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            Material mat = Material.matchMaterial(args[i]);
            if (mat == null || !mat.isBlock()) {
                CommandHelper.sendError(player, "Invalid block type: " + args[i]);
                return true;
            }
            blockCounts.put(mat, 0); // Initialize count
        }

        // Perform count
        int result = CommandHelper.countInSelection(player, block -> {
            Material type = block.getType();
            if (blockCounts.containsKey(type)) {
                blockCounts.put(type, blockCounts.get(type) + 1);
                return true;
            }
            return false;
        });

        if (result == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return true;
        }

        if (result == -2) {
            CommandHelper.sendError(player, "Selection too large!");
            return true;
        }

        if (result == 0) {
            CommandHelper.sendInfo(player, "None of the specified blocks were found in your selection.");
            return true;
        }

        // Build result message
        StringBuilder response = new StringBuilder("Found the following blocks in your selection:\n");
        blockCounts.forEach((mat, count) -> {
            if (count > 0) {
                response.append(mat.name().toLowerCase()).append(" - ").append(count).append("\n");
            }
        });

        CommandHelper.sendSuccess(player, response.toString().trim());
        return true;
    }
}
