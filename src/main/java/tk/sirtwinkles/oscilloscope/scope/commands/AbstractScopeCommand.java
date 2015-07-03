package tk.sirtwinkles.oscilloscope.scope.commands;

import org.bukkit.command.CommandSender;
import tk.sirtwinkles.oscilloscope.OSPlugin;
import tk.sirtwinkles.oscilloscope.commands.CommandCompleter;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

/**
 * Abstract base class for command which control scopes.
 * Created by bob_twinkles on 7/2/15.
 */
public abstract class AbstractScopeCommand implements CommandCompleter {
    Oscilloscope scopeFromArgs(CommandSender sender, String cmd, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(cmd + " needs at least 1 argument, representing the scope ID");
            return null;
        }
        int scopeid;
        try {
            scopeid = Integer.valueOf(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage("First argument must be an integer");
            return null;
        }
        Oscilloscope s = OSPlugin.instance.getScopeRegistry().getScope(scopeid);
        if (s == null) {
            sender.sendMessage("Scope " + scopeid + " does not exist");
            return null;
        }
        return s;
    }
}
