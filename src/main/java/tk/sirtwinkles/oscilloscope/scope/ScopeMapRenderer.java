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

    public ScopeMapRenderer(MapView map) {
        this.map = map;
        mapData = new byte[128 * 128];
        mm = OSPlugin.instance.getMapManager();
        clear();
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        for (int x = 0; x < 128; ++x) {
            for (int y = 0; y < 128; ++y) {
                canvas.setPixel(x, y, mapData[x + y * 128]);
            }
        }
    }

    public void plotPixel(int px, int py, byte color) {
        py = 127 - py; // maps are flipped because reasons
        mapData[px + py * 128] = color;
        mm.markMapDirty(this.map);
    }

    public void clear() {
        Arrays.fill(mapData, (byte)116);
        mm.markMapDirty(this.map);
    }
}
