package dev.flur.ranks.command.commands;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.command.CommandInfo;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.requirement.RequirementFactory;
import dev.flur.ranks.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CommandInfo(
    name = "rankup",
    permission = "ranks.rankup",
    description = "Rank up to the next available rank"
)
public class RankupCommand extends BaseCommand {

    public RankupCommand() {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            HashMap<String, String> nexts = Utils.getNext(Ranks.getPermissions().getPrimaryGroup(p));

            if (!nexts.containsKey(Ranks.getPermissions().getPrimaryGroup(p))) {
                throw new IllegalArgumentException("Player is already at the highest rank");
            }

            if (nexts.size() >= 2 && args.length == 0) {
                p.sendMessage("You have multiple ranks to choose from. Please specify one of the following:");
                nexts.forEach((key, value) -> p.sendMessage(key));
                return true;
            }

            String next;

            if (nexts.size() == 1) {
                next = nexts.entrySet().iterator().next().getKey();
            } else {
                if (nexts.containsKey(args[0])) {
                    next = args[0];
                } else {
                    return false;
                }
            }

            ArrayList<Requirement> requirements = Utils.getRequirements(next, p);

            int i = 0;
            for (Requirement requirement : requirements) {
                if (!requirement.meetsRequirement(p)) {
                    p.sendMessage(RequirementFactory.getRequirementName(requirement) + " wasn't met!");
                } else {
                    i++;
                }
            }

            if (i == requirements.size()) {
                String current = Ranks.getPermissions().getPrimaryGroup(p);
                Ranks.getPermissions().playerAddGroup(p, next);
                Ranks.getPermissions().playerRemoveGroup(p, current);
            }

            return true;
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
