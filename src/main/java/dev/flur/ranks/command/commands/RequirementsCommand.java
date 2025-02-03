package dev.flur.ranks.command.commands;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.requirement.Requirement;
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
import java.util.Objects;

public class RequirementsCommand implements CommandExecutor, TabCompleter {

    public RequirementsCommand() {
        Objects.requireNonNull(Ranks.getPlugin().getCommand("requirements")).setExecutor(this);
        Objects.requireNonNull(Ranks.getPlugin().getCommand("requirements")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            try {
                if (strings.length >= 1) {
                    Ranks.getPlugin().getLogger().info("Requirements for " + strings[0] + ":");
                    ArrayList<Requirement> reqs = Utils.getRequirements(strings[0], p);
                    for (Requirement req : reqs) {
                        p.sendMessage(req.toString());
                    }
                }
            } catch (Exception e) {
                p.sendMessage("Invalid input");
            }
        }
        return true;
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
