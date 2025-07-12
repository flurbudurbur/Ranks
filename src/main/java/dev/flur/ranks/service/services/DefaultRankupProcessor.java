package dev.flur.ranks.service.services;

import dev.flur.ranks.requirement.Requirement;
import dev.flur.ranks.result.RankupOutcome;
import dev.flur.ranks.result.Result;
import dev.flur.ranks.service.PlayerRankService;
import dev.flur.ranks.service.RankProgressionService;
import dev.flur.ranks.service.RankupProcessor;
import dev.flur.ranks.service.RequirementValidator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation of the RankupProcessor interface.
 */
public class DefaultRankupProcessor implements RankupProcessor {

    private final PlayerRankService playerRankService;
    private final RankProgressionService rankProgressionService;
    private final RequirementValidator requirementValidator;
    private final Logger logger;

    public DefaultRankupProcessor(
            @NotNull PlayerRankService playerRankService,
            @NotNull RankProgressionService rankProgressionService,
            @NotNull RequirementValidator requirementValidator,
            @NotNull Logger logger) {
        this.playerRankService = playerRankService;
        this.rankProgressionService = rankProgressionService;
        this.requirementValidator = requirementValidator;
        this.logger = logger;
    }

    @Override
    @NotNull
    public Map<String, String> getAvailableRanks(@NotNull Player player) {
        return rankProgressionService.getAvailableRanks(player);
    }

    @Override
    @NotNull
    public Result<RankupOutcome> processRankup(@NotNull Player player, @NotNull String currentRank, @NotNull String targetRank) {
        List<Requirement> requirements = getRequirementsForRank(player, targetRank);
        List<Requirement> unmetRequirements = requirementValidator.getUnmetRequirements(player, requirements);

        if (unmetRequirements.isEmpty()) {
            return upgradeRank(player, targetRank);
        }

        return Result.failure("Player does not meet all requirements for rank " + targetRank);
    }

    @Override
    @NotNull
    public List<Requirement> getRequirementsForRank(@NotNull Player player, @NotNull String targetRank) {
        return playerRankService.getRequirementsForRank(player, targetRank);
    }

    @Override
    @NotNull
    public List<Requirement> getUnmetRequirementsForRank(@NotNull Player player, @NotNull String targetRank) {
        List<Requirement> requirements = getRequirementsForRank(player, targetRank);
        return requirementValidator.getUnmetRequirements(player, requirements);
    }

    @Override
    @NotNull
    public Result<RankupOutcome> upgradeRank(@NotNull Player player, @NotNull String targetRank) {
        try {
            String currentRank = playerRankService.getCurrentRank(player);
            boolean success = rankProgressionService.upgradeRank(player, targetRank);

            if (success) {
                RankupOutcome outcome = new RankupOutcome(player, currentRank, targetRank, true);
                return Result.success(outcome);
            } else {
                return Result.failure("Failed to upgrade rank for player " + player.getName());
            }
        } catch (Exception e) {
            logger.severe("Failed to upgrade rank for player " + player.getName() + ": " + e.getMessage());
            return Result.failure("Error upgrading rank: " + e.getMessage());
        }
    }
}
