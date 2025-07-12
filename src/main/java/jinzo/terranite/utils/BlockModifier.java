package jinzo.terranite.utils;

import org.bukkit.block.Block;

@FunctionalInterface
public interface BlockModifier {
    boolean apply(Block block);
}
