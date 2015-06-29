package tk.sirtwinkles.oscilloscope.scope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tk.sirtwinkles.oscilloscope.OSPlugin;
import tk.sirtwinkles.oscilloscope.commands.CommandCompleter;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

import java.util.Arrays;
import java.util.List;

/**
 * Command handler and tab completer for scope type setting.
 * This will almost always be run by the player right clicking on the oscilloscope's sign.
 * Created by bob_twinkles on 6/29/15.
 */
public class ScopeSetType implements CommandCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("/scopeMode takes exactly 2 arguments");
            return false;
        }
        try {
            int sid = Integer.valueOf(args[0]);
            Oscilloscope s = OSPlugin.instance.getScopeRegistry().getScope(sid);
            if (s == null) {
                sender.sendMessage("Scope with id " + sid + " does not exist");
                return false;
            }
            if (args[1].equalsIgnoreCase("logic")) {
                s.setType(Oscilloscope.ScopeType.LOGIC);
                OSPlugin.logger.info("Set scope type to logic");
            } else if (args[1].equalsIgnoreCase("analog")) {
                s.setType(Oscilloscope.ScopeType.ANALOG);
                OSPlugin.logger.info("Set scope type to analog");
            } else {
                sender.sendMessage(args[1] + " is not a recognized scope type");
                return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            sender.sendMessage("First argument to /scopeMode must be an integral scope id");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2) {
            return null;
        }
        if (args[1].length() == 0) {
            return Arrays.asList("logic", "analog");
        }
        switch (args[1].charAt(0)) {
            case 'l':
            case 'L':
                return Arrays.asList("logic");
            case 'a':
            case 'A':
                return Arrays.asList("analog");
            default:
                return null;
        }
    }
}
