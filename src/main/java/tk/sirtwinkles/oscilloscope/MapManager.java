package tk.sirtwinkles.oscilloscope;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * A map cache and "dirtyness" tracker.
 * Makes sure we don't burn through all the map IDs, and keeps track of which maps are dity.
 * Dirty maps can be pushed out to players, and are subsequently marked "clean".
 * Created by bob_twinkles on 6/26/15.
 */
public class MapManager {
    public static final String MAP_RESERVED_PATH = "maps.resids";
    private Queue<Short> mapPool;
    private List<Short> reservedIDs;
    private Set<MapView> dirtyMaps;

    public MapManager() {
        this.mapPool = Queues.newArrayDeque();
        this.reservedIDs = OSPlugin.instance.getConfig().getShortList(MAP_RESERVED_PATH);
        mapPool.addAll(reservedIDs);
        this.dirtyMaps = Sets.newHashSet();
    }

    @SuppressWarnings("deprecation")
    public MapView getMap() {
        if (mapPool.isEmpty()) {
            MapView m = Bukkit.createMap(Bukkit.getWorlds().get(0)); // just throw it in the overworld
            m.setCenterX(-2000000); // Make sure the player (almost) never gets an indicator
            m.setCenterZ(-2000000);
            short id = m.getId();
            reservedIDs.add(id);
            return m;
        } else {
            return Bukkit.getMap(mapPool.remove());
        }
    }

    @SuppressWarnings("deprecation")
    public void returnMap(MapView m) {
        mapPool.add(m.getId());
    }

    public void markMapDirty(MapView m) {
        this.dirtyMaps.add(m);
    }

    public void transmitDirtyMaps() {
        for (MapView m : dirtyMaps) {
            World w = m.getWorld();
            for (Player p : w.getPlayers()) {
                p.sendMap(m);
            }
        }
        dirtyMaps.clear();
    }

    public void onDisable() {
        OSPlugin.instance.getConfig().set(MAP_RESERVED_PATH, reservedIDs);
    }
}
