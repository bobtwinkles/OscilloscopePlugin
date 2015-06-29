package tk.sirtwinkles.oscilloscope.task;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.TileEntitySign;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by bob_twinkles on 6/26/15.
 * Dumb hack to work around the fact that Bukkit eats our formatting with it's "sanitization" of setText
 * Needed because Oscilloscopes are created inside the SignChangeEvent handler
 */
public class DeferSignSetTask extends BukkitRunnable{
    Location l;
    IChatBaseComponent[] cs;

    public DeferSignSetTask(Location l, IChatBaseComponent[] cs) {
        this.l = l;
        this.cs = cs;
        if (cs.length > 4) {
            throw new IllegalArgumentException("Can't have more than 4 rows on a sign");
        }
    }

    public DeferSignSetTask(World w, int x, int y, int z, IChatBaseComponent[] cs) {
        this(new Location(w, x, y, z), cs);
    }

    @Override
    public void run() {
        Block b = l.getBlock();
        BlockState bs = b.getState();

        CraftSign craftSign = (CraftSign)bs;
        TileEntitySign sign = craftSign.getTileEntity();
        sign.isEditable = true;
        craftSign.update(true);

        System.arraycopy(cs, 0, sign.lines, 0, cs.length);

        sign.update();
    }
}
