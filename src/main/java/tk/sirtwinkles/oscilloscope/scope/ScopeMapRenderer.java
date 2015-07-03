package tk.sirtwinkles.oscilloscope.scope;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import tk.sirtwinkles.oscilloscope.MapManager;
import tk.sirtwinkles.oscilloscope.OSPlugin;

import java.util.Arrays;

/**
 * Our MapRenderer extension, which allows us to completely control the contents of the maps which form the
 * scope display.
 * Created by bob_twinkles on 6/26/15.
 */
public class ScopeMapRenderer extends MapRenderer {
    private MapView map;
    private byte[] mapData;
    private MapManager mm;

    public static final int MAP_SIZE = 128;
    public static final byte BLACK = 116;

    public ScopeMapRenderer(MapView map) {
        this.map = map;
        mapData = new byte[MAP_SIZE * MAP_SIZE];
        mm = OSPlugin.instance.getMapManager();
        clear();
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        for (int x = 0; x < MAP_SIZE; ++x) {
            for (int y = 0; y < MAP_SIZE; ++y) {
                canvas.setPixel(x, y, mapData[x + y * MAP_SIZE]);
            }
        }
    }

    public void plotPixel(int px, int py, byte color) {
        py = 127 - py; // maps are flipped because reasons
        mapData[px + py * MAP_SIZE] = color;
        mm.markMapDirty(this.map);
    }

    public void clear() {
        Arrays.fill(mapData, BLACK);
        mm.markMapDirty(this.map);
    }

    public MapView getMapView() {
        return map;
    }
}
