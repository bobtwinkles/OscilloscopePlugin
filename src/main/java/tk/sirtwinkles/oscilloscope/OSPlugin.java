package tk.sirtwinkles.oscilloscope;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by bob_twinkles on 6/24/15.
 */
public class OSPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hello, world");
    }

    @Override
    public void onDisable() {
    }
}
