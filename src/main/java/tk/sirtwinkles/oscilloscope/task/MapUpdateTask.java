package tk.sirtwinkles.oscilloscope.task;

import org.bukkit.scheduler.BukkitRunnable;
import tk.sirtwinkles.oscilloscope.OSPlugin;

/**
 * A timed task that asks the map manager to push out  updates.
 * Created by bob_twinkles on 6/28/15.
 */
public class MapUpdateTask extends BukkitRunnable {
    @Override
    public void run() {
        OSPlugin.instance.getMapManager().transmitDirtyMaps();
    }
}
