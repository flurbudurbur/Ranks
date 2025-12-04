package dev.flur.ranks.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SubCommand {

    boolean execute(@NotNull CommandSender sender, @NotNull String[] args);

    @NotNull
    List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args);

    @Nullable
    String getPermission();
}
