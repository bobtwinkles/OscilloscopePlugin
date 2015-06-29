package tk.sirtwinkles.oscilloscope.task;

import org.bukkit.scheduler.BukkitRunnable;
import tk.sirtwinkles.oscilloscope.OSPlugin;

/**
 * A repeated task that ticks all the oscilloscopes in the scope registry.
 * Created by bob_twinkles on 6/28/15.
 */
public class OscilloscopeTickTask extends BukkitRunnable {
    @Override
    public void run() {
        OSPlugin.instance.getScopeRegistry().tickScopes();
    }
}
