package dev.flur.ranks.command.commands;

import dev.flur.commands.CommandInfo;
import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.RanksService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@CommandInfo(
        name = "requirements",
        permission = "ranks.requirements",
        description = "View requirements for ranks"
)
public final class RequirementsCommand extends BaseCommand {

    private final RanksService ranksService;
    private final Logger logger;

    public RequirementsCommand(RanksService ranksService, Logger logger) {
        this.ranksService = ranksService;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String
            s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            try {
                if (strings.length >= 1) {
                    logger.info("Requirements for " + strings[0] + ":");
                    List<Requirement> reqs = ranksService.getRequirements(strings[0], p);
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
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command
            command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }

        Map<String, String> nextRanks = ranksService.getNextRanks(ranksService.getCurrentRank(player));
        if (args.length == 1) {
            return new ArrayList<>(nextRanks.keySet());
        }

        return List.of();
    }
}
