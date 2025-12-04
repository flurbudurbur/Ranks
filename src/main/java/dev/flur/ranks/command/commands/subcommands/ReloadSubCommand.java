package dev.flur.ranks.command.commands.subcommands;

import dev.flur.ranks.Ranks;
import dev.flur.ranks.command.SubCommand;
import dev.flur.ranks.message.Messages;
import dev.flur.ranks.service.MessageService;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReloadSubCommand implements SubCommand {

    private static final String PERMISSION = "ranks.admin.reload";

    private final Ranks plugin;
    private final MessageService messageService;

    public ReloadSubCommand(@NotNull Ranks plugin, @NotNull MessageService messageService) {
        this.plugin = plugin;
        this.messageService = messageService;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        boolean fullReload = args.length > 0 && "full".equalsIgnoreCase(args[0]);

        if (fullReload) {
            plugin.reloadConfig();
            messageService.sendMessage(sender, Messages.RELOAD_FULL_COMPLETED);
        }

        plugin.reload();
        messageService.sendMessage(sender, Messages.RELOAD_COMPLETED);

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return List.of("full").stream()
                    .filter(opt -> opt.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }

    @Override
    public @Nullable String getPermission() {
        return PERMISSION;
    }
}
