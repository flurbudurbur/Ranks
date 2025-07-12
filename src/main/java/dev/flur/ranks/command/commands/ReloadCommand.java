package dev.flur.ranks.command.commands;

import dev.flur.commands.CommandInfo;
import dev.flur.ranks.Ranks;
import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.message.Messages;
import dev.flur.ranks.service.MessageService;
import dev.flur.ranks.service.ServiceContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandInfo(
        name = "reload",
        permission = "ranks.admin.reload",
        description = "Reload the plugin's configuration files",
        usage = "/ranks reload [full]"
)
public class ReloadCommand extends BaseCommand {

    private final Ranks plugin;
    private final ServiceContainer serviceContainer;
    private final MessageService messageService;

    public ReloadCommand(@NotNull Ranks plugin, @NotNull ServiceContainer serviceContainer, @NotNull MessageService messageService) {
        this.plugin = plugin;
        this.serviceContainer = serviceContainer;
        this.messageService = messageService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        boolean fullReload = args.length > 0 && "full".equalsIgnoreCase(args[0]);

        if (sender instanceof ConsoleCommandSender) {
            return handleReload(sender, fullReload);
        }

        if (sender instanceof Player player && player.hasPermission("ranks.admin.reload")) {
            return handleReload(player, fullReload);
        }

        messageService.sendMessage(sender, Messages.NO_PERMISSION);
        return true;
    }

    private boolean handleReload(@NotNull CommandSender sender, boolean fullReload) {
        if (fullReload) {
            // Reload the plugin's config
            plugin.reloadConfig();
            messageService.sendMessage(sender, Messages.RELOAD_FULL_COMPLETED);
        }

        // Reload all services through the ServiceContainer
        serviceContainer.reload();

        if (!serviceContainer.isHealthy()) return false;

        messageService.sendMessage(sender, Messages.RELOAD_COMPLETED);

        return true;
    }
}
