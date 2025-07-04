package dev.flur.ranks.command.commands;

import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.command.CommandInfo;
import dev.flur.ranks.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "ranks", permission = "ranks.view", description = "View available ranks")
public class RanksCommand extends BaseCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        Utils.getRankList().forEach(sender::sendMessage);
        return true;
    }
}