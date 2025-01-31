package dev.flur.ranks.command.commands;

import dev.flur.ranks.Ranks;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.flur.ranks.utils.Init.getRankList;

public class RanksCommand implements CommandExecutor, TabCompleter {

    public RanksCommand() {
        Ranks.getPlugin().getCommand("ranks").setExecutor(this);
        Ranks.getPlugin().getCommand("ranks").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            p.sendMessage("Hello, " + p.getName());
            getRankList().forEach(p::sendMessage);
        } else {
            commandSender.sendMessage("Hello, console");
            getRankList().forEach(commandSender::sendMessage);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        return List.of();
    }
}
