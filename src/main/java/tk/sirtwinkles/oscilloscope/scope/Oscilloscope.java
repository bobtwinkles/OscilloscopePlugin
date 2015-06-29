package tk.sirtwinkles.oscilloscope.scope;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItemFrame;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import tk.sirtwinkles.oscilloscope.OSPlugin;
import tk.sirtwinkles.oscilloscope.task.DeferSignSetTask;

import java.util.Collection;

/**
 * Keeps track of persistent oscilloscope state.
 * Created by bob_twinkles on 6/24/15.
 */
public class Oscilloscope {
    // Bold and blue!
    public static final ChatModifier TITLE_FORMAT = new ChatModifier();
    // Bold green
    public static final ChatModifier CONFIG_ACTIVE = new ChatModifier();
    // Aqua
    public static final ChatModifier CONFIG_SELECTABLE = new ChatModifier();
    // Red
    public static final ChatModifier CONFIG_INVALID = new ChatModifier();

    static {
        TITLE_FORMAT.setBold(true);
        TITLE_FORMAT.setColor(EnumChatFormat.BLUE);

        CONFIG_ACTIVE.setBold(true);
        CONFIG_ACTIVE.setColor(EnumChatFormat.GREEN);

        CONFIG_SELECTABLE.setColor(EnumChatFormat.AQUA);

        CONFIG_INVALID.setColor(EnumChatFormat.RED);
    }

    public static final int MAX_DISPLAY_SIZE = 16;

    private static final int BB_QUERY_SIZE = (MAX_DISPLAY_SIZE / 2) + 1;

    /**
     * What type of scope are we?
     */
    public enum ScopeType {
        LOGIC,
        ANALOG
    }

    private Location displayRootLocation;
    private BiMap<Integer, Location> probes;
    private final ScopeType type;
    private IChatBaseComponent[] textDisplay;
    private ScopeMapRenderer[] display;
    private int stepX;
    private int stepZ;
    private int displayWidth;
    private int displayHeight;

    public Oscilloscope(Block displayRoot, String[] lines) {
        this.displayRootLocation = displayRoot.getLocation();
        this.probes = HashBiMap.create();

        // Figure out what type of oscilloscope we're going to default to
        Sign bs = (Sign)displayRoot.getState();
        String type = lines[1];
        if (type.equalsIgnoreCase("logic")) {
            this.type = ScopeType.LOGIC;
        } else {
            this.type = ScopeType.ANALOG;
        }

        double searchx, searchz;
        stepX = stepZ = 0;
        BlockFace facing = ((org.bukkit.material.Sign)bs.getData()).getFacing();
        switch(facing) {
            case NORTH:
                searchx = -BB_QUERY_SIZE;
                searchz = 0.5;
                stepX = -1;
                stepZ = 0;
                break;
            case EAST:
                searchx = 0.5;
                searchz = -BB_QUERY_SIZE;
                stepX = 0;
                stepZ = -1;
                break;
            case SOUTH:
                searchx = BB_QUERY_SIZE;
                searchz = -0.5;
                stepX = 1;
                stepZ = 0;
                break;
            case WEST:
                searchx = -0.5;
                searchz = BB_QUERY_SIZE;
                stepX = 0;
                stepZ = 1;
                break;
            default:
                OSPlugin.logger.warning("Wall sign was not facing sensible direction!");
                OSPlugin.logger.warning("Oscilloscope will have 0 channels");
                searchx = 0; searchz = 0;
                break;
        }
        World w = this.displayRootLocation.getWorld();
        Location searchCenter = displayRootLocation.clone().add(searchx, BB_QUERY_SIZE + 1.5, searchz);
        Collection<Entity> queryRes = w.getNearbyEntities(searchCenter, Math.abs(searchx) + 1, BB_QUERY_SIZE, Math.abs(searchz) + 1);

        MapView[] maps = new MapView[MAX_DISPLAY_SIZE * MAX_DISPLAY_SIZE];

        // Find the item frames we'll be using
        for (Entity e : queryRes) {
            if (e instanceof CraftItemFrame) {
                CraftItemFrame cie = (CraftItemFrame) e;
                if (!cie.getFacing().equals(facing)) continue;

                Location frameLoc = cie.getLocation();
                MapView m = OSPlugin.instance.getMapManager().getMap();
                int mx, my;
                my = frameLoc.getBlockY() - displayRootLocation.getBlockY() - 1;
                if (stepX == 0) { // east/west orientation
                    mx = stepZ * (frameLoc.getBlockZ() - displayRootLocation.getBlockZ());
                } else { // north/south orientation
                    mx = stepX * (frameLoc.getBlockX() - displayRootLocation.getBlockX());
                }
                if (mx < 0 || mx >= MAX_DISPLAY_SIZE) continue;
                if (my < 0 || my >= MAX_DISPLAY_SIZE) continue;
                maps[mx + my * MAX_DISPLAY_SIZE] = m;
                ItemStack is = new ItemStack(Material.MAP);
                is.setDurability(m.getId());
                cie.setItem(is);
                cie.setRotation(Rotation.NONE);
            }
        }

        // detect height and width
        displayHeight = MAX_DISPLAY_SIZE;
        for (int x = 0; x < MAX_DISPLAY_SIZE; ++x) {
            if (maps[x] == null) {
                displayWidth = x;
                break;
            }
            for (int y = 0; y < displayHeight; ++y) {
                if (maps[x + y * MAX_DISPLAY_SIZE] == null) {
                    if (y < displayHeight) {
                        displayHeight = y;
                        break;
                    }
                }
            }
        }

        // Create renderers
        display = new ScopeMapRenderer[displayWidth * displayHeight];
        for (int x = 0; x < displayWidth; ++x) {
            for (int y = 0; y < displayHeight; ++y) {
                MapView m = maps[x + y * MAX_DISPLAY_SIZE];
                OSPlugin.logger.info(x + " " + y);
                ScopeMapRenderer smr = new ScopeMapRenderer(m);
                m.addRenderer(smr);
                display[x + y * displayWidth] = smr;
            }
        }

        // Initialze the display to some garbage
        for (int x = 0; x < 128 * displayWidth; ++x) {
            for (int y = 0; y < 128 * displayHeight; ++y) {
                plotPixel(x, y, (byte)(x ^ y));
            }
        }

        OSPlugin.logger.info(searchCenter.toString());
        OSPlugin.logger.info(queryRes.toString());

        textDisplay = new IChatBaseComponent[3];
        updateDisplay();
    }

    public void tick() {
        for (ScopeMapRenderer smr : display) {
            smr.clear();
        }
        long wt = OSPlugin.instance.getServer().getWorlds().get(0).getTime();
        int plotHeight = (displayHeight * 128) - 2;
        for (int t = 0; t < displayWidth * 128; ++t) {
            float tf = (wt + t) /  20.f;
            float f = MathHelper.sin((float)(2 * Math.PI * tf / 16)) / 2;
            int y = (int)(f * plotHeight) + (plotHeight / 2) + 1;
            plotPixel(t, y, (byte)126);
        }
    }

    private void plotPixel(int x, int y, byte color) {
        if (x < 0 || x >= displayWidth * 128) {
            return;
        }
        if (y < 0 || y >= displayHeight * 128) {
            return;
        }
        int mx = x / 128;
        int my = y / 128;
        int px = x - (mx * 128);
        int py = y - (my * 128);
        ScopeMapRenderer smr = display[mx + my * displayWidth];
        smr.plotPixel(px, py, color);
    }

    /**
     * Updates our internal textDisplay representation, and submits it to the task queue to be updated.
     */
    private void updateDisplay() {
        ChatComponentText title = new ChatComponentText("Oscilloscope");
        ChatComponentText modeRoot = new ChatComponentText("");

        title.setChatModifier(TITLE_FORMAT);

        ChatComponentText logic = new ChatComponentText("[LOG]");
        ChatComponentText analog = new ChatComponentText("[ANA]");
        switch (this.type){
            case LOGIC:
                logic.setChatModifier(CONFIG_ACTIVE);
                analog.setChatModifier(CONFIG_SELECTABLE);
                break;
            case ANALOG:
                logic.setChatModifier(CONFIG_SELECTABLE);
                analog.setChatModifier(CONFIG_ACTIVE);
                break;
        }
        modeRoot.addSibling(logic);
        modeRoot.addSibling(analog);

        ChatComponentText size = new ChatComponentText(displayWidth + " " + displayHeight);

        textDisplay[0] = title;
        textDisplay[1] = modeRoot;
        textDisplay[2] = size;

        (new DeferSignSetTask(displayRootLocation, textDisplay)).runTask(OSPlugin.instance);
    }
}
