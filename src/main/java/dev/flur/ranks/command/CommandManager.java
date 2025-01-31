package dev.flur.ranks.command;

import dev.flur.ranks.command.commands.RankupCommand;
import dev.flur.ranks.command.commands.RanksCommand;
import dev.flur.ranks.command.commands.RequirementsCommand;

public class CommandManager {

    public CommandManager() {
        new RanksCommand();
        new RankupCommand();
        new RequirementsCommand();
    }
}
