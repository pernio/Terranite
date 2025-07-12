package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class countTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        final boolean allBlocksMode = args.length < 2;
        Map<Material, Integer> blockCounts = new HashMap<>();

        if (!allBlocksMode) {
            for (int i = 1; i < args.length; i++) {
                Material mat = Material.matchMaterial(args[i]);
                if (mat == null || !mat.isBlock()) {
                    CommandHelper.sendError(player, "Invalid block type: " + args[i]);
                    return true;
                }
                blockCounts.put(mat, 0);
            }
        }

        int result = CommandHelper.countInSelection(player, (Block block) -> {
            Material type = block.getType();

            if (allBlocksMode && type == Material.AIR) return false;

            if (allBlocksMode) {
                blockCounts.put(type, blockCounts.getOrDefault(type, 0) + 1);
                return true;
            }

            if (blockCounts.containsKey(type)) {
                blockCounts.put(type, blockCounts.get(type) + 1);
                return true;
            }

            return false;
        });

        if (result == -1) {
            CommandHelper.sendError(player, "You must set both Position 1 and Position 2 first.");
            return false;
        }

        if (result == -2) return false;

        if (blockCounts.isEmpty() || blockCounts.values().stream().allMatch(c -> c == 0)) {
            CommandHelper.sendInfo(player, "No matching blocks found in selection.");
            return false;
        }

        StringBuilder response = new StringBuilder("Found the following blocks in your selection:\n");
        blockCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(entry -> response.append(entry.getKey().name().toLowerCase())
                        .append(" - ")
                        .append(entry.getValue())
                        .append("\n"));

        CommandHelper.sendSuccess(player, response.toString().trim());
        return true;
    }
}
