package jinzo.snorf.listeners;

import jinzo.snorf.Snorf;
import jinzo.snorf.events.OutlineTask;
import jinzo.snorf.utils.OutlineTaskManager;
import jinzo.snorf.utils.SelectionManager;
import jinzo.snorf.utils.CommandHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SelectionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isSnorfer(item)) return;

        Block block = event.getClickedBlock();
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_BLOCK) {
            SelectionManager.setPos1(player, block);
            CommandHelper.sendSuccess(player, "Position 1 set!");
            showOutline(player, block);
            event.setCancelled(true);
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            SelectionManager.setPos2(player, block);
            CommandHelper.sendSuccess(player, "Position 2 set!");
            showOutline(player, block);
            event.setCancelled(true);
        }

        OutlineTaskManager.start(player);
    }

    private boolean isSnorfer(ItemStack item) {
        if (item == null || item.getType() != Material.ARROW) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta.displayName() == null) return false;

        Component expectedName = Component.text("Snorfer", NamedTextColor.GOLD);
        return meta.displayName().equals(expectedName);
    }

    public static void showOutline(Player player, Block block) {
        block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                block.getLocation().add(0.5, 0.5, 0.5),
                10, 0.5, 0.5, 0.5, 0.05);
    }
}
