package jinzo.terranite.commands;

import jinzo.terranite.Terranite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class AliasCommandExecutor implements CommandExecutor, TabCompleter {
    private final String subcommand;

    public AliasCommandExecutor(String subcommand) {
        this.subcommand = subcommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Forward to the main command handler with the subcommand as first argument
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = subcommand;
        System.arraycopy(args, 0, newArgs, 1, args.length);

        return Terranite.getInstance()
                .getCommand("terranite")
                .getExecutor()
                .onCommand(sender, command, label, newArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Prepend subcommand
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = subcommand;
        System.arraycopy(args, 0, newArgs, 1, args.length);

        return Terranite.getInstance()
                .getCommand("terranite")
                .getTabCompleter()
                .onTabComplete(sender, command, alias, newArgs);
    }
}