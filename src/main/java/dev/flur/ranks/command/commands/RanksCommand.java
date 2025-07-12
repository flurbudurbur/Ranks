package dev.flur.ranks.command.commands;

import dev.flur.commands.CommandInfo;
import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.service.RanksService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "ranks", permission = "ranks.view", description = "View available ranks")
public final class RanksCommand extends BaseCommand {

    private final RanksService rankService;

    public RanksCommand(RanksService rankService) {
        this.rankService = rankService;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        rankService.getAllRanks().forEach(sender::sendMessage);
        return true;
    }
}
