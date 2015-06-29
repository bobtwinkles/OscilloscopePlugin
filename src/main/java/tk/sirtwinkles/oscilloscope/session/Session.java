package tk.sirtwinkles.oscilloscope.session;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tk.sirtwinkles.oscilloscope.OSPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains stateful information about the player.
 * Created by bob_twinkles on 6/24/15.
 */
public class Session {
    private Player player;

    public Session(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
