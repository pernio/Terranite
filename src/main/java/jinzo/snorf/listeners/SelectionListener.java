package jinzo.snorf.listeners;

import jinzo.snorf.utils.CommandHelper;
import jinzo.snorf.utils.OutlineTaskManager;
import jinzo.snorf.utils.SelectionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class SelectionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();

        if (!CommandHelper.isSnorfer(player.getInventory().getItemInMainHand())) return;

        Location blockLocation = event.getClickedBlock().getLocation();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            SelectionManager.setPos1(player, blockLocation);
            CommandHelper.sendSuccess(player, "Position 1 set!");
            showOutline(player, blockLocation);
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            SelectionManager.setPos2(player, blockLocation);
            CommandHelper.sendSuccess(player, "Position 2 set!");
            showOutline(player, blockLocation);
            event.setCancelled(true);
        }

        OutlineTaskManager.start(player);
    }

    private void sendSuccess(Player player, String message) {
        Component prefix = Component.text("[Snorf] ", NamedTextColor.GOLD);
        Component body = Component.text(message, NamedTextColor.DARK_GREEN);
        player.sendMessage(prefix.append(body));
    }

    public static void showOutline(Player player, Location location) {
        player.spawnParticle(Particle.HAPPY_VILLAGER,
                location.clone().add(0.5, 0.5, 0.5),
                10, 0.5, 0.5, 0.5, 0.05);
    }
}
