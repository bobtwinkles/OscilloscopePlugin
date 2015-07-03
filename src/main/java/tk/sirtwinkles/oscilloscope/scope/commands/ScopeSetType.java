package tk.sirtwinkles.oscilloscope.scope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Command handler and tab completer for scope type setting.
 * This will almost always be run by the player right clicking on the oscilloscope's sign.
 * Created by bob_twinkles on 6/29/15.
 */
public class ScopeSetType extends AbstractScopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(label + " takes exactly 2 arguments");
            return false;
        }
        Oscilloscope s = scopeFromArgs(sender, label, args);
        if (s == null) {
            return false;
        }
        if (args[1].equalsIgnoreCase("logic")) {
            s.setType(Oscilloscope.ScopeType.LOGIC);
        } else if (args[1].equalsIgnoreCase("analog")) {
            s.setType(Oscilloscope.ScopeType.ANALOG);
        } else {
            sender.sendMessage(args[1] + " is not a recognized scope type");
            return false;
        }
        return true;
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
                return Collections.singletonList("logic");
            case 'a':
            case 'A':
                return Collections.singletonList("analog");
            default:
                return null;
        }
    }
}
