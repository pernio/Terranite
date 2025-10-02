package jinzo.terranite.commands;

import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.MaskManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class maskTerra {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            CommandHelper.sendError(sender, "Only players can use this.");
            return false;
        }

        String searchedMaterial = (args.length > 1 && !args[1].isEmpty()) ? args[1] : "air";

        Material material = CommandHelper.findMaterial(player, searchedMaterial);
        if (material == null) material = Material.AIR;
        MaskManager.setMask(player, material);

        CommandHelper.sendSuccess(player, "Mask successfully applied on type " + material.name().toLowerCase());

        return true;
    }
}
