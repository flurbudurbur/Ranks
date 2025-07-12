package dev.flur.ranks.command.commands;

import dev.flur.commands.CommandInfo;
import dev.flur.ranks.command.BaseCommand;
import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.service.RankupNotifier;
import dev.flur.ranks.service.RankupProcessor;
import dev.flur.ranks.service.RankupValidator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@CommandInfo(
        name = "rankup",
        permission = "ranks.rankup",
        description = "Rank up to the next available rank"
)
public final class RankupCommand extends BaseCommand {

    private final RankupValidator rankupValidator;
    private final RankupProcessor rankupProcessor;
    private final RankupNotifier rankupNotifier;
    private final Logger logger;

    public RankupCommand(
            @NotNull RankupValidator rankupValidator,
            @NotNull RankupProcessor rankupProcessor,
            @NotNull RankupNotifier rankupNotifier,
            @NotNull Logger logger) {
        super();
        this.rankupValidator = rankupValidator;
        this.rankupProcessor = rankupProcessor;
        this.rankupNotifier = rankupNotifier;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            rankupNotifier.sendPlayerOnlyMessage(sender);
            return true;
        }

        try {
            return handleRankupCommand(player, args);
        } catch (Exception e) {
            rankupNotifier.sendErrorMessage(player, e.getMessage());
            return true;
        }
    }

    private boolean handleRankupCommand(@NotNull Player player, @NotNull String[] args) {
        String currentRank = rankupValidator.getCurrentRank(player);
        if (rankupValidator.hasValidCurrentRank(player)) {
            rankupNotifier.sendCurrentRankErrorMessage(player);
            return true;
        }

        Map<String, String> availableRanks = rankupProcessor.getAvailableRanks(player);

        if (availableRanks.isEmpty()) {
            rankupNotifier.sendHighestRankMessage(player);
            return true;
        }

        if (rankupValidator.shouldShowRankOptions(availableRanks, args)) {
            rankupNotifier.showAvailableRanks(player, availableRanks);
            return true;
        }

        String targetRank = rankupValidator.determineTargetRank(availableRanks, args);
        if (targetRank == null) {
            rankupNotifier.showInvalidRankMessage(player, availableRanks);
            return true;
        }

        return processRankup(player, currentRank, targetRank);
    }

    private boolean processRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank) {
        List<Requirement> unmetRequirements = rankupValidator.getUnmetRequirementsForRank(player, targetRank);

        if (unmetRequirements.isEmpty()) {
            return rankupProcessor.processRankup(player, currentRank, targetRank)
                    .onSuccess(outcome -> {
                        rankupNotifier.sendRankupSuccessMessage(player, targetRank);
                        rankupNotifier.broadcastRankup(player, currentRank, targetRank);
                    })
                    .onFailure(errorMessage -> {
                        rankupNotifier.sendRankupFailedMessage(player);
                        logger.warning("Rankup failed: " + errorMessage);
                    })
                    .isSuccess();
        } else {
            rankupNotifier.notifyUnmetRequirements(player, unmetRequirements);
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

        // Call hasValidCurrentRank outside the try-catch to ensure it's always called
        boolean hasValidCurrentRank;
        try {
            hasValidCurrentRank = rankupValidator.hasValidCurrentRank(player);
        } catch (Exception e) {
            logger.severe("Error in tab completion: " + e.getMessage());
            return List.of();
        }

        if (hasValidCurrentRank) {
            return List.of();
        }

        try {
            Map<String, String> availableRanks = rankupProcessor.getAvailableRanks(player);
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
