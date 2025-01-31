package dev.flur.ranks.command.commands;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RankupCommand implements CommandExecutor, TabCompleter {

    public RankupCommand() {
        Ranks.getPlugin().getCommand("rankup").setExecutor(this);
        Ranks.getPlugin().getCommand("rankup").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            HashMap<String, String> nexts = Utils.getNext(Ranks.getPermissions().getPrimaryGroup(p));
            if (nexts.size() >= 2 && args.length == 0) {;
                p.sendMessage("You have multiple ranks to choose from. Please specify one of the following:");
                nexts.forEach((key, value) -> p.sendMessage(key));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HashMap<String, String> nexts = Utils.getNext(Ranks.getPermissions().getPrimaryGroup((Player) sender));
        if (args.length == 1) {
            return new ArrayList<>(nexts.keySet());
        }

        return List.of();
    }
}
