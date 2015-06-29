package tk.sirtwinkles.oscilloscope.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import tk.sirtwinkles.oscilloscope.OSPlugin;

/**
 * Listens for sign creation events, and turns signs that match a pattern in to oscilloscopes.
 * Created by bob_twinkles on 6/24/15.
 */
public class OscilloscopeCreationListner implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent bpe) {
        Block placed = bpe.getBlock();
        if (placed.getType() == Material.WALL_SIGN) {
            String player = bpe.getPlayer().getName();

            OSPlugin.logger.info(player + " placed a sign");
        }
    }

    @EventHandler
    public void onSignText(SignChangeEvent sce) {
        String player = sce.getPlayer().getName();
        String[] lines = sce.getLines();

        if (lines[0].equalsIgnoreCase("[oscilloscope]") || lines[0].equalsIgnoreCase("[osc]")) {
            OSPlugin.logger.info("Player " + player + " placed an oscilloscope");

            sce.setLine(0, "Please Hold");
            // Lines 0 and 1 are used for configuration
            for (int i = 2; i < 4; ++i) {
                sce.setLine(i, "");
            }

            Block b = sce.getBlock();
            OSPlugin.instance.getScopeRegistry().tryCreateScope(b, lines);
        }
    }

}
