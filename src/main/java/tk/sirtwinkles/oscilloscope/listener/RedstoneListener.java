package tk.sirtwinkles.oscilloscope.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import tk.sirtwinkles.oscilloscope.OSPlugin;

/**
 * Listens for redstone events, pushes updates to the scopes.
 * Created by bob_twinkles on 6/24/15.
 */
public class RedstoneListener implements Listener {
    @EventHandler
    public void onRedstoneChange(BlockRedstoneEvent e) {
        Location l = e.getBlock().getLocation();
        OSPlugin.instance.getLogger().info(l.getBlock().toString());
        // TODO: detect if probe location, and push updates to the scopes.
    }
}
