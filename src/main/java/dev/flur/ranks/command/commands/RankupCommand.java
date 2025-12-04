package dev.flur.ranks.command.commands;

import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.service.RankupService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class RankupCommand extends BaseCommand {

    private final RankupService rankupService;
    private final Logger logger;

    public RankupCommand(@NotNull RankupService rankupService, @NotNull Logger logger) {
        super();
        this.rankupService = rankupService;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            rankupService.sendPlayerOnlyMessage(sender);
            return true;
        }

        try {
            return handleRankupCommand(player, args);
        } catch (Exception e) {
            rankupService.sendErrorMessage(player, e.getMessage());
            return true;
        }
    }

    private boolean handleRankupCommand(@NotNull Player player, @NotNull String[] args) {
        String currentRank = rankupService.getCurrentRank(player);
        if (currentRank.isEmpty()) {
            rankupService.sendCurrentRankErrorMessage(player);
            return true;
        }

        Map<String, String> availableRanks = rankupService.getAvailableRanks(player);

        if (availableRanks.isEmpty()) {
            rankupService.sendHighestRankMessage(player);
            return true;
        }

        if (rankupService.shouldShowRankOptions(availableRanks, args)) {
            rankupService.showAvailableRanks(player, availableRanks);
            return true;
        }

        String targetRank = rankupService.determineTargetRank(availableRanks, args);
        if (targetRank == null) {
            rankupService.showInvalidRankMessage(player, availableRanks);
            return true;
        }

        return processRankup(player, currentRank, targetRank);
    }

    private boolean processRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank) {
        if (rankupService.canRankup(player, targetRank)) {
            return rankupService.processRankup(player, targetRank)
                    .onSuccess(outcome -> {
                        rankupService.sendRankupSuccessMessage(player, targetRank);
                        rankupService.broadcastRankup(player, currentRank, targetRank);
                    })
                    .onFailure(errorMessage -> {
                        rankupService.sendRankupFailedMessage(player);
                        logger.warning("Rankup failed: " + errorMessage);
                    })
                    .isSuccess();
        } else {
            rankupService.notifyUnmetRequirements(player, targetRank);
            return false;
        }
    }

    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }

        if (args.length != 1) {
            return List.of();
        }

        try {
            String currentRank = rankupService.getCurrentRank(player);
            if (currentRank.isEmpty()) {
                return List.of();
            }

            Map<String, String> availableRanks = rankupService.getAvailableRanks(player);
            String partial = args[0].toLowerCase();

            return availableRanks.keySet().stream()
                    .filter(rank -> rank.toLowerCase().startsWith(partial))
                    .toList();
        } catch (Exception e) {
            logger.severe("Error in tab completion: " + e.getMessage());
            return List.of();
        }
    }
}
