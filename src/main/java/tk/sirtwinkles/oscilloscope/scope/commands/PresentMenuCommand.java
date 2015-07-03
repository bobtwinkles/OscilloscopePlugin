package tk.sirtwinkles.oscilloscope.scope.commands;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import tk.sirtwinkles.oscilloscope.scope.Oscilloscope;

import java.util.List;

/**
 * Presents a textual menu when run
 * Created by bob_twinkles on 6/29/15.
 */
public class PresentMenuCommand extends AbstractScopeCommand {
    private static final String[][] commands = {
            {"Analog Mode", "[ANA]", "/settype %d analog"},
            {"Logic Mode",  "[LOG]", "/settype %d logic"},
            {"Add a probe", "[ADD PROBE]", "/addprobe %d"},
            {"Start recording", "[REC]", "/startrecording %d"}
    };
    private static final String prompt = ChatColor.AQUA + "====[Command Menu]====";

    public static final ChatModifier RESET = new ChatModifier();

    static {
        RESET.setColor(EnumChatFormat.WHITE);
        RESET.setBold(false);
        RESET.setItalic(false);
        RESET.setChatClickable(null);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Oscilloscope s = scopeFromArgs(sender, label, args);
        if (s == null) {
            return false;
        }
        int scopeid = s.getID();
        sender.sendMessage(prompt);
        if (sender instanceof Player) {
            CraftPlayer player = (CraftPlayer) sender;
            EntityPlayer eplayer = player.getHandle();
            for (String[] cmd : commands) {
                String action = String.format(cmd[2], scopeid);
                eplayer.sendMessage(playerMessage(cmd[0], cmd[1], action));
            }
        } else {
            for (String[] cmd : commands) {
                String action = String.format(cmd[2], scopeid);
                sender.sendMessage(formatCommand(cmd[0], cmd[1], action));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    IChatBaseComponent playerMessage(String intro, String button, String command) {
        IChatBaseComponent clickable = new ChatComponentText(button);
        IChatBaseComponent continuation = new ChatComponentText(intro);

        ChatModifier clickableModifier = new ChatModifier();
        ChatClickable clickableAction = new ChatClickable(ChatClickable.EnumClickAction.RUN_COMMAND, command);

        clickableModifier.setColor(EnumChatFormat.GREEN);
        clickableModifier.setChatClickable(clickableAction);
        clickable.setChatModifier(clickableModifier);

        continuation.setChatModifier(RESET);

        clickable.addSibling(continuation);
        return clickable;
    }

    private String formatCommand(String intro, String button, String command) {
        return String.format("%s %s: %s", button, intro, command);
    }
}
