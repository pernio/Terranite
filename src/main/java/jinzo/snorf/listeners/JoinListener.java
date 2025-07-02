package jinzo.snorf.listeners;

import jinzo.snorf.events.OutlineTask;
import jinzo.snorf.utils.OutlineTaskManager;
import jinzo.snorf.utils.SelectionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        var sel = SelectionManager.getSelection(player);

        if (sel.pos1 != null && sel.pos2 != null) {
            OutlineTaskManager.start(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        OutlineTaskManager.stop(event.getPlayer());
    }
}
