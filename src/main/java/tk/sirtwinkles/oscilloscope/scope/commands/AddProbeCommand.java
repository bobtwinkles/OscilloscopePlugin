package tk.sirtwinkles.oscilloscope.scope.commands;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.sirtwinkles.oscilloscope.OSPlugin;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

import java.util.List;

/**
 * Takes either a location or, when issued by a player, puts that player in to selection mode.
 * Created by bob_twinkles on 7/2/15.
 */
public class AddProbeCommand extends AbstractScopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Oscilloscope scope = scopeFromArgs(sender, label, args);
        if (scope == null) {
            return false;
        }
        if (!scope.hasChannelsAvailable()) {
            sender.sendMessage("Scope " + scope.getID() + " has no more channels available");
        }
        if (args.length >= 4 || !(sender instanceof Player)) {
            if (args.length < 4) {
                sender.sendMessage("You aren't a player, so " + label + " needs 4 arguments");
                return false;
            }
            int x, y, z;
            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException ex) {
                sender.sendMessage("Position arguments must be integers");
                return false;
            }
            World w;
            Server s = OSPlugin.instance.getServer();
            if (args.length >= 5) {
                w = s.getWorld(args[4]);
            } else {
                sender.sendMessage("You didn't specify a world: defaulting to same world as scope");
                w = scope.getWorld();
            }
            sender.sendMessage("You have selected (" + w.getName() + ": " + x + ", " + y + ", " + z + ") for scope " + scope.getID());
            if (!scope.addProbe(w, x, y, z)) {
                sender.sendMessage("Something has gone horribly wrong");
            }
            return true;
        } else {
            Player player = (Player) sender;
            OSPlugin.instance.getSessionForPlayer(player).setSelectionMode(scope.getID());
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
