package tk.sirtwinkles.oscilloscope.scope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

import java.util.List;

/**
 * Command to kick off scope recording
 * Created by bob_twinkles on 7/3/15.
 */
public class StartRecordingCommand extends AbstractScopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Oscilloscope s = scopeFromArgs(sender, label, args);
        if (s == null) {
            return false;
        }
        s.startRecording();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
