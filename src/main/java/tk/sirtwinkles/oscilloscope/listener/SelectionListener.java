package tk.sirtwinkles.oscilloscope.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import tk.sirtwinkles.oscilloscope.OSPlugin;
import tk.sirtwinkles.oscilloscope.session.Session;

/**
 * Listens for player interaction events, and usues the player's session to select oscilloscope probe locations.
 * Created by bob_twinkles on 6/24/15.
 */
public class SelectionListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent ev) {
        Player p = ev.getPlayer();
        Session s = OSPlugin.instance.getSessionForPlayer(p);
        if (s.isSelecting()) {
            Location l = ev.getClickedBlock().getLocation();
            s.selectLocation(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
        }
    }
}
