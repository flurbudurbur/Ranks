package dev.flur.ranks.command;

import dev.flur.ranks.command.commands.RankupCommand;
import dev.flur.ranks.command.commands.RanksCommand;

public class CommandManager {

    public CommandManager() {
        new RanksCommand();
        new RankupCommand();
    }
}
