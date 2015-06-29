package tk.sirtwinkles.oscilloscope;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tk.sirtwinkles.oscilloscope.commands.CommandCompleter;
import tk.sirtwinkles.oscilloscope.listener.OscilloscopeCreationListner;
import tk.sirtwinkles.oscilloscope.listener.RedstoneListener;
import tk.sirtwinkles.oscilloscope.listener.SelectionListener;
import tk.sirtwinkles.oscilloscope.scope.ScopeRegistry;
import tk.sirtwinkles.oscilloscope.scope.commands.ScopeSetType;
import tk.sirtwinkles.oscilloscope.session.Session;
import tk.sirtwinkles.oscilloscope.task.MapUpdateTask;
import tk.sirtwinkles.oscilloscope.task.OscilloscopeTickTask;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Main plugin entrypoint, and a place to stash various persistant structures.
 * Created by bob_twinkles on 6/24/15.
 */
public class OSPlugin extends JavaPlugin {
    public static OSPlugin instance;
    public static Logger logger;

    private Map<Player, Session> sessions;
    private ScopeRegistry scopeRegistry;
    private MapManager manager;
    private MapUpdateTask mapUpdateTask;
    private OscilloscopeTickTask oscilloscopeTickTask;

    @Override
    public void onEnable() {
        getLogger().info("Setting global oscilloscope instance to " + this.toString());
        instance = this;
        logger = this.getLogger();
        reloadConfig();

        sessions = new HashMap<Player, Session>();

        registerListner(new RedstoneListener());
        registerListner(new SelectionListener());
        registerListner(new OscilloscopeCreationListner());

        scopeRegistry = new ScopeRegistry();
        manager = new MapManager();

        mapUpdateTask = new MapUpdateTask();
        oscilloscopeTickTask = new OscilloscopeTickTask();

        mapUpdateTask.runTaskTimer(this, 0, 4);
        oscilloscopeTickTask.runTaskTimer(this, 1, 4);

        registerCommand("setType", new ScopeSetType());
    }

    @Override
    public void onDisable() {
        logger.info("################################################################################");
        logger.info("#                              DISABLING OSCILLOSCOPE                          #");
        logger.info("################################################################################");
        manager.onDisable();
        mapUpdateTask.cancel();
        scopeRegistry.destroyScopes();
        saveConfig();
    }

    public Session getSessionForPlayer(Player p) {
        if (sessions.containsKey(p)) {
            return sessions.get(p);
        } else {
            Session s = new Session(p);
            sessions.put(p, s);
            return s;
        }
    }

    public MapManager getMapManager() {
        return manager;
    }

    public ScopeRegistry getScopeRegistry() {
        return scopeRegistry;
    }

    /**
     * Convienience method for registering listners
     * @param l listner to register
     */
    private void registerListner(Listener l) {
        getServer().getPluginManager().registerEvents(l, this);
    }

    private void registerCommand(String cmd, CommandCompleter c) {
        PluginCommand pc = getCommand(cmd);
        pc.setExecutor(c);
        pc.setTabCompleter(c);
    }
}
