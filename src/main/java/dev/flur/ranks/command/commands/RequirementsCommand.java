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
import java.util.List;

public class RequirementsCommand implements CommandExecutor, TabCompleter {

    public RequirementsCommand() {
        Ranks.getPlugin().getCommand("requirements").setExecutor(this);
        Ranks.getPlugin().getCommand("requirements").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            try {
                if (strings.length >= 1) {
                    Ranks.getPlugin().getLogger().info("Requirements for " + strings[0] + ":");
                    ArrayList<Requirement> reqs = Utils.getRequirements(strings[0], p);
                    if (reqs == null) {
                        p.sendMessage("Invalid rank");
                        return true;
                    }
                    for (Requirement req : reqs) {
                        p.sendMessage(req.toString());
                    }
                }
            } catch (Exception e) {
                p.sendMessage("Invalid input");
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
