package jinzo.terranite.listeners;

import jinzo.terranite.utils.CommandHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.function.Consumer;

import java.util.logging.Level;

import static jinzo.terranite.utils.CommandHelper.isTerraWand;

public class PickupListener implements Listener {
    private final JavaPlugin plugin;
    private final boolean isFolia;

    public PickupListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.isFolia = detectFolia();
    }

    /**
     * Detect if server is running Folia by checking for Folia-specific classes.
     */
    private boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            plugin.getLogger().log(Level.INFO, "Folia environment detected.");
            return true;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.INFO, "Running on standard Bukkit/Paper environment.");
            return false;
        }
    }

    private void handleWandLoss(Player player) {
        if (!player.hasPermission("terranite.use")) return;

        for (ItemStack item : player.getInventory().getContents()) {
            if (isTerraWand(item)) return;
        }

        Component msg = Component.text("You lost your Terra Wand. ", NamedTextColor.RED)
                .append(Component.text("Click here to get it back.", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/s wand")));

        player.sendMessage(msg);
    }

    /**
     * Runs a task later with delay=1 tick, using Folia's scheduler if on Folia,
     * otherwise falls back to Bukkit scheduler.
     */
    private void runLater(Player player, Runnable task) {
        plugin.getLogger().info("runLater called for player: " + player.getName());
        if (isFolia) {
            player.getScheduler().runDelayed(plugin,
                    scheduledTask -> task.run(), // <- run the task here
                    task,
                    1L
            );
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, 1L);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack dropped = event.getItemDrop().getItemStack();

        if (isTerraWand(dropped)) {
            event.getItemDrop().remove();
            runLater(player, () -> handleWandLoss(player));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack draggedItem = event.getOldCursor();
        if (!isTerraWand(draggedItem)) return;

        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topInventory.getSize()) {
                runLater(player, () -> {
                    removeWandFromInventory(topInventory);
                    handleWandLoss(player);
                });
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack cursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        Inventory clicked = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        boolean isContainer = clicked != null && !(clicked.getHolder() instanceof Player);

        if (cursor != null && isTerraWand(cursor) && isContainer) {
            runLater(player, () -> {
                removeWandFromInventory(topInventory);
                handleWandLoss(player);
            });
            return;
        }

        if (event.getClick().isShiftClick() && currentItem != null && isTerraWand(currentItem)) {
            runLater(player, () -> {
                removeWandFromInventory(topInventory);
                handleWandLoss(player);
            });
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (!isTerraWand(hand)) return;

        switch (event.getRightClicked().getType()) {
            case ITEM_FRAME, GLOW_ITEM_FRAME, ARMOR_STAND -> {
                event.setCancelled(true);
                player.getInventory().remove(hand);
                runLater(player, () -> handleWandLoss(player));
            }
            default -> {
                if (!player.hasPermission("terranite.use")) {
                    event.setCancelled(true);
                    player.getInventory().remove(hand);
                    runLater(player, () -> handleWandLoss(player));
                }
            }
        }
    }

    private void removeWandFromInventory(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (isTerraWand(item)) {
                inv.setItem(i, null);
            }
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();

        if (isTerraWand(item)) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack arrow = event.getConsumable();
        if (arrow == null) return;

        if (isTerraWand(arrow)) {
            event.setCancelled(true);
            ItemStack invArrow = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (invArrow != null && isTerraWand(invArrow)) {
                invArrow.setAmount(invArrow.getAmount() - 1);
                if (invArrow.getAmount() <= 0)
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
            }
            handleWandLoss(player);
        }
    }

    private boolean isAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
