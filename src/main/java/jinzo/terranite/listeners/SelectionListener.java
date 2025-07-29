package jinzo.terranite.listeners;

import jinzo.terranite.Terranite;
import jinzo.terranite.utils.CommandHelper;
import jinzo.terranite.utils.ConfigManager;
import jinzo.terranite.utils.OutlineTaskManager;
import jinzo.terranite.utils.SelectionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.Particle;

public class SelectionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getPlayer().hasPermission("terranite.use")) return;

        Player player = event.getPlayer();

        if (!CommandHelper.isTerraWand(player.getInventory().getItemInMainHand())) return;

        Location blockLocation = event.getClickedBlock().getLocation();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            SelectionManager.setPos1(player, blockLocation);
            CommandHelper.sendSuccess(player, "Position 1 set!");
            showOutline(player, blockLocation);
            CommandHelper.playSound(player, blockLocation);
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            SelectionManager.setPos2(player, blockLocation);
            CommandHelper.sendSuccess(player, "Position 2 set!");
            showOutline(player, blockLocation);
            CommandHelper.playSound(player, blockLocation);
            event.setCancelled(true);
        }

        OutlineTaskManager.start(player);
    }

    public static void showOutline(Player player, Location location) {
        ConfigManager config = Terranite.getInstance().getConfiguration();

        player.spawnParticle(
                Particle.DUST,
                location.clone().add(0.5, 0.5, 0.5),
                20,
                0.5, 0.5, 0.5,
                0,
                config.selectEffectDust
        );
    }
}
