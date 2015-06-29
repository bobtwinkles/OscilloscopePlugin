package tk.sirtwinkles.oscilloscope;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

import java.util.List;

/**
 * Keeps track of all the scopes we know about
 * Created by bob_twinkles on 6/25/15.
 */
public class ScopeRegistry {
    private BiMap<Location, Integer> locationMap;
    private List<Oscilloscope> scopes;


    public ScopeRegistry() {
        scopes = Lists.newArrayList();
        locationMap = HashBiMap.create();
    }

    public Oscilloscope getScope(Location l) {
        // We already know the root
        int scopeID;
        if (locationMap.containsKey(l)) {
            scopeID = locationMap.get(l);
            return scopes.get(scopeID);
        } else {
            List<MetadataValue> values = l.getBlock().getMetadata("oscilloscope");
            for (MetadataValue v : values) {
                scopeID = v.asInt();
                if (scopeID < scopes.size() && scopes.get(scopeID) != null) {
                    return scopes.get(scopeID);
                }
            }
            // All searches failed, there is no oscilloscope here
            return null;
        }
    }

    public boolean tryCreateScope(Block b, String[] lines) {
        if (b.getType() == Material.WALL_SIGN) {
            Oscilloscope scope = new Oscilloscope(b, lines);
            int scopeID = insertScope(scope);
            b.setMetadata("oscilloscope", new FixedMetadataValue(OSPlugin.instance, scopeID));
            locationMap.put(b.getLocation(), scopeID);
            return true;
        } else {
            return false;
        }
    }

    public void tickScopes() {
        for (Oscilloscope s : scopes) {
            s.tick();
        }
    }

    private int insertScope(Oscilloscope scope) {
        int idx = scopes.size();
        scopes.add(scope);
        return idx;
    }
}
