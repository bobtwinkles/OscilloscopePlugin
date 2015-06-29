package tk.sirtwinkles.oscilloscope.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * Interfacne for implementing both CommandExecutor and TabCompleter
 * Basically a convinenence that allows us to type check things better.
 * Created by bob_twinkles on 6/29/15.
 */
public interface CommandCompleter extends CommandExecutor, TabCompleter {
}
