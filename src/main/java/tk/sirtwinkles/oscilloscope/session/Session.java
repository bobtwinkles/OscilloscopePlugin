package tk.sirtwinkles.oscilloscope.session;

import org.bukkit.World;
import org.bukkit.entity.Player;
import tk.sirtwinkles.oscilloscope.OSPlugin;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

/**
 * Contains stateful information about the player.
 * Created by bob_twinkles on 6/24/15.
 */
public class Session {
    private Player player;
    private int targetScope;

    public Session(Player player) {
        this.player = player;
        this.targetScope = -1;
    }

    public void setSelectionMode(int selectionTarget) {
        player.sendMessage("You have entered selection mode for scope " + selectionTarget);
        this.targetScope = selectionTarget;
    }

    public boolean isSelecting() {
        return this.targetScope != -1;
    }

    public void selectLocation(World w, int x, int y, int z) {
        if (!this.isSelecting()) {
            throw new IllegalStateException("Scope select a location when not selecting");
        }
        Oscilloscope scope = OSPlugin.instance.getScopeRegistry().getScope(targetScope);
        if (scope == null) {
            player.sendMessage("Unfortunately, scope " + targetScope + " has been destroyed since you entered selection mode");
        }
        player.sendMessage("You have selected (" + w.getName() + ": " + x + ", " + y + ", " + z + ") for scope " + targetScope);
        scope.addProbe(w, x, y, z);
        this.targetScope = -1;
    }
}
