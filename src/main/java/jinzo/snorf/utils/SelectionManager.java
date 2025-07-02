package jinzo.snorf.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SelectionManager {
    private static final Map<Player, Selection> selections = new HashMap<>();

    public static void setPos1(Player player, Block block) {
        getSelection(player).pos1 = block;
    }

    public static void setPos2(Player player, Block block) {
        getSelection(player).pos2 = block;
    }

    public static Selection getSelection(Player player) {
        return selections.computeIfAbsent(player, p -> new Selection());
    }

    public static class Selection {
        public Block pos1 = null;
        public Block pos2 = null;
    }
}
