package me.elaineqheart.auctionHouse.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DynamicCommand extends Command {

    private final CommandExecutor executor;
    private final TabCompleter tabCompleter;

    public DynamicCommand(String name, String description, List<String> aliases, CommandExecutor executor, TabCompleter tabCompleter) {
        super(name);
        this.executor = executor;
        this.tabCompleter = tabCompleter;
        setDescription(description);
        setAliases(aliases);
        setPermission("auctionhouse.ah");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return executor.onCommand(sender, this, commandLabel, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (tabCompleter != null) {
            List<String> result = tabCompleter.onTabComplete(sender, this, alias, args);
            return result == null ? Collections.emptyList() : result;
        }
        return Collections.emptyList();
    }
}
