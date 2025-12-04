package dev.flur.ranks.command.commands;

import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.command.SubCommand;
import dev.flur.ranks.service.RanksService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RanksCommand extends BaseCommand {

    private final RanksService rankService;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public RanksCommand(@NotNull RanksService rankService) {
        this.rankService = rankService;
    }

    public void registerSubCommand(@NotNull String name, @NotNull SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length > 0) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                if (!hasPermission(sender, subCommand.getPermission())) {
                    sender.sendMessage("You don't have permission to use this command.");
                    return true;
                }
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.execute(sender, subArgs);
            }
        }

        rankService.getAllRanks().forEach(sender::sendMessage);
        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return subCommands.entrySet().stream()
                    .filter(entry -> hasPermission(sender, entry.getValue().getPermission()))
                    .map(Map.Entry::getKey)
                    .filter(name -> name.startsWith(prefix))
                    .toList();
        }

        if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null && hasPermission(sender, subCommand.getPermission())) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.tabComplete(sender, subArgs);
            }
        }

        return List.of();
    }

    private boolean hasPermission(@NotNull CommandSender sender, String permission) {
        if (permission == null) {
            return true;
        }
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        return sender instanceof Player player && player.hasPermission(permission);
    }
}
